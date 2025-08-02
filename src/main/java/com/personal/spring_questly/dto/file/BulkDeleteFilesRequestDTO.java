package com.personal.spring_questly.dto.file;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record BulkDeleteFilesRequestDTO(
        @NotNull
        @NotEmpty
        List<UUID> fileIds
) {
}

