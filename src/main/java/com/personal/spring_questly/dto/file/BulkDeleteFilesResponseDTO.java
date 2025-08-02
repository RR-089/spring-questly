package com.personal.spring_questly.dto.file;

import lombok.Builder;

import java.util.List;

@Builder
public record BulkDeleteFilesResponseDTO(
        List<FileDTO> deletedFiles,

        List<FileDTO> undeletedFiles
) {
}
