package com.personal.spring_questly.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FileService {
    String getFileMimeType(File file);

    byte[] readAllBytes(File file);

    String getFileDisposition(File file);

    File getFileByModuleNameAndFileName(String moduleName, String fileName);

    List<com.personal.spring_questly.model.File> bulkUploadFiles(String moduleName, List<MultipartFile> files);
}
