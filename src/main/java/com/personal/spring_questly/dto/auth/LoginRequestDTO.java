package com.personal.spring_questly.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequestDTO(
        @NotBlank
        @Email
        @Schema(description = "Email", example = "test123@gmail.com")
        String email,

        @NotBlank
        @Schema(description = "Password")
        String password
) {
}
