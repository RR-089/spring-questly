package com.personal.spring_questly.service;

import com.personal.spring_questly.dto.file.BulkDeleteFilesResponseDTO;
import com.personal.spring_questly.dto.file.FileDTO;
import com.personal.spring_questly.exception.CustomException.BadRequestException;
import com.personal.spring_questly.exception.CustomException.NotFoundException;
import com.personal.spring_questly.repository.FileRepository;
import com.personal.spring_questly.service.impl.FileServiceImpl;
import org.junit.jupiter.api.AfterEach;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static com.personal.spring_questly.utils.FieldUtils.injectField;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTests {
    private static final String UPLOAD_DIR = "./uploads/";
    private static final String MODULE_NAME = "testmodule";

    @Mock
    private FileRepository fileRepository;
    @InjectMocks
    private FileServiceImpl fileService;

    @BeforeEach
    void setup() {
        injectField(fileService, "uploadDir", UPLOAD_DIR);
    }

    @AfterEach
    void cleanup() throws IOException {
        // Clean up any files created under ./uploads/testmodule/
        Path moduleDir = Paths.get(UPLOAD_DIR, MODULE_NAME);
        if (Files.exists(moduleDir)) {
            Files.walk(moduleDir)
                 .sorted(Comparator.reverseOrder())
                 .map(Path::toFile)
                 .forEach(File::delete);
        }
    }

    @Test
    void testMapToDto_Success() {
        com.personal.spring_questly.model.File file =
                com.personal.spring_questly.model.File.builder()
                                                      .id(UUID.randomUUID())
                                                      .moduleName("user")
                                                      .name("random-name")
                                                      .uri("/user/random-name")
                                                      .type("image/jpg")
                                                      .size(1000D)
                                                      .index(null)
                                                      .build();

        FileDTO result = FileServiceImpl.mapToDTO(file);

        assertEquals(result.name(), file.getName());
        assertEquals(result.moduleName(), file.getModuleName());
        assertEquals(result.uri(), file.getUri());
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
        String moduleName = MODULE_NAME;
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
                spyService.bulkUploadFiles(MODULE_NAME, multipartFiles));

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
                fileService.bulkUploadFiles(MODULE_NAME, multipartFiles);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNull(result.get(0).getIndex());
        assertTrue(result.get(0).getName().endsWith(".png"));

        assertEquals(MODULE_NAME, result.get(0).getModuleName());

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
                fileService.bulkUploadFiles(MODULE_NAME, multipartFiles);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(0, result.get(0).getIndex());
        assertEquals(1, result.get(1).getIndex());
        assertTrue(result.get(0).getName().endsWith(".png"));
        assertTrue(result.get(1).getName().endsWith(".jpg"));

        assertEquals(MODULE_NAME, result.get(0).getModuleName());

        verify(fileRepository, times(1)).saveAll(anyList());
    }


    @Test
    void bulkDeleteFiles_shouldHandleExistingAndMissingFilesProperly() throws IOException {
        // Arrange
        UUID id1 = UUID.randomUUID(); // will be deleted
        UUID id2 = UUID.randomUUID(); // will fail (not created)

        com.personal.spring_questly.model.File file1 = createTestFile(id1, "file1.txt");
        com.personal.spring_questly.model.File file2 = createTestFile(id2, "file2.txt");

        List<UUID> ids = List.of(id1, id2);

        when(fileRepository.findAllById(ids)).thenReturn(List.of(file1, file2));

        // Create file1 only
        createFileOnDisk(file1);
        // Do not create file2

        // Act
        BulkDeleteFilesResponseDTO response = fileService.bulkDeleteFiles(ids);

        // Assert
        assertEquals(1, response.deletedFiles().size());
        assertEquals(1, response.undeletedFiles().size());

        assertEquals("file1.txt", response.deletedFiles().get(0).name());
        assertEquals("file2.txt", response.undeletedFiles().get(0).name());

        verify(fileRepository).findAllById(ids);
        verify(fileRepository).deleteAll(List.of(file1));

        // Ensure file1 is deleted
        assertTrue(Files.notExists(getPath(file1)));
    }

    private com.personal.spring_questly.model.File createTestFile(UUID id, String name) {
        com.personal.spring_questly.model.File file = new com.personal.spring_questly.model.File();
        file.setId(id);
        file.setModuleName(MODULE_NAME);
        file.setName(name);
        return file;
    }

    private void createFileOnDisk(com.personal.spring_questly.model.File file) throws IOException {
        Path dirPath = Paths.get(UPLOAD_DIR, file.getModuleName());
        Files.createDirectories(dirPath);

        Path filePath = dirPath.resolve(file.getName());
        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }

        System.out.println("Created file at: " + filePath.toAbsolutePath());
    }

    private Path getPath(com.personal.spring_questly.model.File file) {
        return Paths.get(UPLOAD_DIR, file.getModuleName(), file.getName());
    }

}
