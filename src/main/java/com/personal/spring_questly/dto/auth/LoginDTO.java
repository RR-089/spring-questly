package com.personal.spring_questly.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginDTO(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) {
}
