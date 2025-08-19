package com.personal.spring_questly.controller;

import com.personal.spring_questly.dto.common.ApiResponseDTO;
import com.personal.spring_questly.dto.file.BulkDeleteFilesRequestDTO;
import com.personal.spring_questly.dto.file.BulkDeleteFilesResponseDTO;
import com.personal.spring_questly.dto.file.BulkUploadFilesRequestDTO;
import com.personal.spring_questly.service.FileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;

    @GetMapping(value = "{moduleName}/{fileName:.+}")
    public ResponseEntity<?> getFile(
            @PathVariable("moduleName") String moduleName,
            @PathVariable("fileName") String fileName
    ) {
        File data = fileService.getFileByModuleNameAndFileName(moduleName, fileName);

        return ResponseEntity.ok()
                             .header("Content-Type", fileService.getFileMimeType(data))
                             .header("Content-Disposition", fileService.getFileDisposition(data))
                             .body(fileService.readAllBytes(data));
    }

    @PostMapping(value = "bulk-upload")
    public ResponseEntity<ApiResponseDTO<Object>> uploadFiles(
            @Valid @ModelAttribute BulkUploadFilesRequestDTO dto
    ) {
        HttpStatus status = HttpStatus.CREATED;
        List<com.personal.spring_questly.model.File> data =
                fileService.bulkUploadFiles(dto.moduleName(), dto.files());

        Object responseData = data.size() > 1 ?
                data.stream()
                    .map(com.personal.spring_questly.model.File::getUri)
                    .toList()
                : data.get(0).getUri();

        return ResponseEntity.status(status).body(
                ApiResponseDTO.builder()
                              .status(status.value())
                              .message("Bulk upload successful")
                              .data(responseData)
                              .build()
        );
    }

    @DeleteMapping(value = "bulk-delete")
    public ResponseEntity<ApiResponseDTO<BulkDeleteFilesResponseDTO>> bulkDeleteFiles(
            @Valid @RequestBody BulkDeleteFilesRequestDTO dto
    ) {
        BulkDeleteFilesResponseDTO data = fileService.bulkDeleteFiles(dto.fileIds());

        ApiResponseDTO<BulkDeleteFilesResponseDTO> response = ApiResponseDTO.<BulkDeleteFilesResponseDTO>builder()
                                                                            .status(HttpStatus.OK.value())
                                                                            .message("Bulk delete successful")
                                                                            .data(data)
                                                                            .build();

        return ResponseEntity.ok(response);

    }

}
