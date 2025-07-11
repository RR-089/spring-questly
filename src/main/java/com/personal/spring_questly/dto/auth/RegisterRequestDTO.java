package com.personal.spring_questly.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record RegisterRequestDTO(
        @NotBlank
        @Email
        @Schema(description = "Email", example = "test123@gmail.com")
        String email,

        @NotBlank
        @Schema(description = "Password")
        String password,

        @Length(min = 3)
        @Schema(description = "First name user. Example: 'John'", example = "John")
        String firstName,

        @Length(min = 3)
        @Schema(description = "Last name user. Example: 'Doe'", example = "Doe")
        String lastName,

        @NotNull
        @Schema(description = "Indicates whether the user wants to be a quester",
                example = "true")
        Boolean isQuester,

        @Schema(description = "Indicates whether the user wants to be a requester",
                example = "true", nullable = true)
        boolean isRequester
) {
}
