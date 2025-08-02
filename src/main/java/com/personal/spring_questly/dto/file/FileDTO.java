package com.personal.spring_questly.dto.file;

import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record FileDTO(
        UUID id,

        String moduleName,

        String name,

        String type,

        Double size,

        Integer index,

        String uri,

        Instant uploadedAt
) {
}
