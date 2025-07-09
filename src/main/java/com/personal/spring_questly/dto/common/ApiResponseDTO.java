package com.personal.spring_questly.dto.common;

import lombok.Builder;

@Builder
public record ApiResponseDTO<T>(
        long status,
        String message,
        T data
) {
}
