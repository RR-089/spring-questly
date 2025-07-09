package com.personal.spring_questly.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record RegisterDTO(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String password,

        @Length(min = 3)
        String firstName,

        @Length(min = 3)
        String lastName,

        @NotNull
        Boolean isQuester,

        boolean isRequester
) {
}
