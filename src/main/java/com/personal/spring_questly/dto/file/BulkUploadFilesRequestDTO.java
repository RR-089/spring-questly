package com.personal.spring_questly.dto.file;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public record BulkUploadFilesRequestDTO(
        @NotBlank
        String moduleName,

        @NotNull
        @NotEmpty
        List<MultipartFile> files
) {
}
