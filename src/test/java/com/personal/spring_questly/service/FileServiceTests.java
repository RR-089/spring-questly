package com.personal.spring_questly.service;

import com.personal.spring_questly.exception.CustomException.BadRequestException;
import com.personal.spring_questly.exception.CustomException.NotFoundException;
import com.personal.spring_questly.repository.FileRepository;
import com.personal.spring_questly.service.impl.FileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.personal.spring_questly.utils.FieldUtils.injectField;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTests {
    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    void setup() {
        injectField(fileService, "uploadDir", "./uploads/");
    }

    @Test
    void testGetFileMimeType_BadRequest() {
        BadRequestException errors = assertThrows(BadRequestException.class, () ->
                fileService.getFileMimeType(null));

        assertNotNull(errors);
        assertEquals(HttpStatus.BAD_REQUEST, errors.getStatus());
        assertEquals("Cannot get file mime type", errors.getMessage());
    }

    @Test
    void testGetFileMimeType_Success() throws IOException {
        File tempFile = File.createTempFile("testfile", ".txt");
        tempFile.deleteOnExit();

        String mimeType = fileService.getFileMimeType(tempFile);

        assertNotNull(mimeType);
        assertEquals("text/plain", mimeType);
    }

    @Test
    void testReadAllBytes_BadRequest() {
        BadRequestException errors = assertThrows(BadRequestException.class, () ->
                fileService.readAllBytes(null));

        assertNotNull(errors);
        assertEquals(HttpStatus.BAD_REQUEST, errors.getStatus());
        assertEquals("Cannot read bytes file", errors.getMessage());
    }


    @Test
    void testReadAllBytes_Success() throws IOException {
        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);

        File tempImage = File.createTempFile("temp-image", ".png");
        tempImage.deleteOnExit();

        ImageIO.write(image, "png", tempImage);

        byte[] bytes = fileService.readAllBytes(tempImage);

        assertNotNull(bytes);
        assertTrue(bytes.length > 0);
    }

    @Test
    void testGetFileDisposition_Success_Attachment() {
        FileServiceImpl spyService = spy(fileService);
        File file = new File("testfile.png");

        doReturn("text/plain").when(spyService).getFileMimeType(file);

        String disposition = spyService.getFileDisposition(file);

        assertNotNull(disposition);
        assertEquals("attachment; testfile.png", disposition);
    }

    @Test
    void testGetFileDisposition_Success_Inline() {
        FileServiceImpl spyService = spy(fileService);
        File image = new File("testimage.png");

        doReturn("image/png").when(spyService).getFileMimeType(image);

        String disposition = spyService.getFileDisposition(image);

        assertNotNull(disposition);
        assertEquals("inline; testimage.png", disposition);
    }

    @Test
    void testGetFileByModuleNameAndFileName_NotFound() throws IOException {
        NotFoundException errors = assertThrows(NotFoundException.class, () ->
                fileService.getFileByModuleNameAndFileName(null, null));

        assertNotNull(errors);
        assertEquals(HttpStatus.NOT_FOUND, errors.getStatus());
        assertEquals("Resource not found", errors.getMessage());
    }


    @Test
    void testGetFileByModuleNameAndFileName_Success() throws IOException {
        String moduleName = "testmodule";
        String fileName = "testimage.png";

        File moduleDir = new File("./uploads/" + moduleName);
        moduleDir.mkdirs();

        File testFile = new File(moduleDir, fileName);
        testFile.createNewFile();
        testFile.deleteOnExit();

        File result = fileService.getFileByModuleNameAndFileName(moduleName, fileName);

        assertNotNull(result);
        assertTrue(result.exists());
        assertEquals(testFile.getAbsolutePath(), result.getAbsolutePath());
    }

    @Test
    void testBulkUploadFiles_Exception_ShouldCleanUpAndThrow() throws IOException {
        FileServiceImpl spyService = spy(fileService);

        MultipartFile mockFile1 = mock(MultipartFile.class);
        when(mockFile1.getOriginalFilename()).thenReturn("test1.png");
        when(mockFile1.getSize()).thenReturn(1024L);
        when(mockFile1.getContentType()).thenReturn("image/png");
        doNothing().when(mockFile1).transferTo(any(File.class));

        MultipartFile mockFile2 = mock(MultipartFile.class);
        when(mockFile2.getOriginalFilename()).thenReturn("test2.png");
        doThrow(new IOException("Simulated failure")).when(mockFile2).transferTo(any(File.class));

        doReturn(new File("fake-file-path")).when(spyService)
                                            .getFileByModuleNameAndFileName(anyString(), anyString());

        List<MultipartFile> multipartFiles = List.of(mockFile1, mockFile2);

        BadRequestException errors = assertThrows(BadRequestException.class, () ->
                spyService.bulkUploadFiles("testmodule", multipartFiles));

        assertNotNull(errors);
        assertEquals("Bulk upload files failed", errors.getMessage());
        verify(fileRepository, never()).saveAll(anyList());
    }


    @Test
    void testBulkUploadFiles_Success_OnlyOne() throws IOException {
        MultipartFile mockFile1 = mock(MultipartFile.class);
        when(mockFile1.getOriginalFilename()).thenReturn("test1.png");
        when(mockFile1.getSize()).thenReturn(1024L);
        when(mockFile1.getContentType()).thenReturn("image/png");
        doNothing().when(mockFile1).transferTo(any(File.class));

        List<MultipartFile> multipartFiles = List.of(mockFile1);

        when(fileRepository.saveAll(anyList()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        List<com.personal.spring_questly.model.File> result =
                fileService.bulkUploadFiles("testmodule", multipartFiles);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getIndex());
        assertTrue(result.get(0).getName().endsWith(".png"));

        assertEquals("testmodule", result.get(0).getModuleName());

        verify(fileRepository, times(1)).saveAll(anyList());
    }


    @Test
    void testBulkUploadFiles_Success_MoreThanOne() throws IOException {
        MultipartFile mockFile1 = mock(MultipartFile.class);
        when(mockFile1.getOriginalFilename()).thenReturn("test1.png");
        when(mockFile1.getSize()).thenReturn(1024L);
        when(mockFile1.getContentType()).thenReturn("image/png");
        doNothing().when(mockFile1).transferTo(any(File.class));

        MultipartFile mockFile2 = mock(MultipartFile.class);
        when(mockFile2.getOriginalFilename()).thenReturn("test2.jpg");
        when(mockFile2.getSize()).thenReturn(1024L);
        when(mockFile2.getContentType()).thenReturn("image/jpeg");
        doNothing().when(mockFile2).transferTo(any(File.class));

        List<MultipartFile> multipartFiles = List.of(mockFile1, mockFile2);

        when(fileRepository.saveAll(anyList()))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        List<com.personal.spring_questly.model.File> result =
                fileService.bulkUploadFiles("testmodule", multipartFiles);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(0, result.get(0).getIndex());
        assertEquals(1, result.get(1).getIndex());
        assertTrue(result.get(0).getName().endsWith(".png"));
        assertTrue(result.get(1).getName().endsWith(".jpg"));

        assertEquals("testmodule", result.get(0).getModuleName());

        verify(fileRepository, times(1)).saveAll(anyList());
    }


}
