package com.personal.spring_questly.service.impl;

import com.personal.spring_questly.exception.CustomException.BadRequestException;
import com.personal.spring_questly.exception.CustomException.NotFoundException;
import com.personal.spring_questly.repository.FileRepository;
import com.personal.spring_questly.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public String getFileMimeType(File file) {
        log.info("Get file mime type...");
        try {
            return Files.probeContentType(file.toPath());
        } catch (Exception ex) {
            throw new BadRequestException("Cannot get file mime type", null);
        }
    }

    @Override
    public byte[] readAllBytes(File file) {
        log.info("read all bytes file...");
        try {
            return Files.readAllBytes(file.toPath());
        } catch (Exception ex) {
            throw new BadRequestException("Cannot read bytes file", null);

        }
    }

    @Override
    public String getFileDisposition(File file) {
        log.info("Get file disposition");

        String prefix;

        if (this.getFileMimeType(file).startsWith("image/")) {
            prefix = "inline";
        } else {
            prefix = "attachment";
        }

        return String.format("%s; %s", prefix, file.getName());
    }

    @Override
    public File getFileByModuleNameAndFileName(String moduleName, String fileName) {
        log.info("Get file: {}/{}", moduleName, fileName);

        File file =
                new File(uploadDir + File.separator + moduleName + File.separator + fileName);

        if (!file.exists() || !file.isFile()) {
            throw new NotFoundException("Resource not found", null);
        }

        return file;
    }

    @Override
    public List<com.personal.spring_questly.model.File> bulkUploadFiles(String moduleName, List<MultipartFile> files) {
        log.info("Bulk upload files for module: {}", moduleName);

        List<com.personal.spring_questly.model.File> newFiles = new ArrayList<>();

        try {
            for (MultipartFile file : files) {
                String fileExtension = this.getFileExtension(file);

                String randomFileName =
                        UUID.randomUUID() + "-" + System.currentTimeMillis() + fileExtension;

                String filePath = this.getFilePath(moduleName, randomFileName);

                file.transferTo(new File(filePath));

                String fileUri = this.getFileUri(moduleName, randomFileName);

                newFiles.add(com.personal.spring_questly.model.File.builder()
                                                                   .name(randomFileName)
                                                                   .moduleName(moduleName)
                                                                   .type(file.getContentType())
                                                                   .size((double) file.getSize() / 1024)
                                                                   .index(files.size() > 1 ? files.indexOf(file) : null)
                                                                   .uri(fileUri)
                                                                   .build());
            }
        } catch (Exception ex) {
            for (com.personal.spring_questly.model.File uploadedFile : newFiles) {
                File currUploadedFile =
                        this.getFileByModuleNameAndFileName(uploadedFile.getModuleName(), uploadedFile.getName());

                if (currUploadedFile.exists()) {
                    currUploadedFile.delete();
                }
            }

            throw new BadRequestException("Bulk upload files failed", null);
        }

        return fileRepository.saveAll(newFiles);

    }

    private String getFilePath(String moduleName, String randomFileName) throws IOException {
        File moduleDir = moduleName != null ?
                new File(uploadDir + File.separator + moduleName)
                : new File(uploadDir);

        if (!moduleDir.exists() && !moduleDir.mkdirs()) {
            throw new IOException("Failed to create directory: " + moduleDir.getAbsolutePath());
        }

        return moduleDir.getAbsolutePath() + File.separator + randomFileName;
    }

    private String getFileExtension(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || !originalFileName.contains(".")) {
            return "";
        }

        return originalFileName.substring(originalFileName.lastIndexOf("."));
    }

    private String getFileUri(String moduleName, String randomFileName) {
        return (moduleName != null && !moduleName.isBlank()
                ? File.separator + moduleName : "")
                + File.separator + randomFileName;
    }
}
