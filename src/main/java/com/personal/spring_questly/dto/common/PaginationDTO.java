package com.personal.spring_questly.dto.common;

import lombok.Builder;

@Builder
public record PaginationDTO<T>(
        long totalPages,
        long totalRecords,
        T data
) {
}
