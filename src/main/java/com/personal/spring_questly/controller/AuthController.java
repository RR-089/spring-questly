package com.personal.spring_questly.controller;

import com.personal.spring_questly.dto.auth.LoginDTO;
import com.personal.spring_questly.dto.auth.RegisterDTO;
import com.personal.spring_questly.dto.common.ApiResponseDTO;
import com.personal.spring_questly.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/register")
    public ResponseEntity<ApiResponseDTO<String>> register(
            @Valid @RequestBody RegisterDTO dto
    ) {
        HttpStatus status = HttpStatus.CREATED;
        String data = authService.register(dto);

        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                                                        .status(status.value())
                                                        .message("Register user is successful")
                                                        .data(data)
                                                        .build();

        return ResponseEntity.status(status).body(response);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<ApiResponseDTO<String>> login(
            @Valid @RequestBody LoginDTO dto
    ) {
        String data = authService.login(dto);

        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                                                        .status(HttpStatus.OK.value())
                                                        .message("Login user is successful")
                                                        .data(data)
                                                        .build();

        return ResponseEntity.ok(response);
    }

}
