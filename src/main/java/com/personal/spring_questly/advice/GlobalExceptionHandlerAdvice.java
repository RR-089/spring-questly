package com.personal.spring_questly.advice;

import com.personal.spring_questly.dto.common.ApiResponseDTO;
import com.personal.spring_questly.exception.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandlerAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleCustomException(
            CustomException ex,
            HttpServletRequest req
    ) {
        log.info("Custom error at route: {}", req.getRequestURL());
        log.info("Instance error name: {}", ex.getClass().getName());

        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
                                                        .status(ex.getStatus().value())
                                                        .message(ex.getMessage())
                                                        .data(ex.getData())
                                                        .build();

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handlerArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest req
    ) {
        log.info("Method argument not valid error at: {}", req.getRequestURL());
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, String> errors = new HashMap<>();

        for (FieldError fieldError : ex.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
                                                        .status(status.value())
                                                        .message("Some values are invalid")
                                                        .data(errors)
                                                        .build();

        return ResponseEntity.status(status).body(response);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleGeneralException(
            Exception ex,
            HttpServletRequest req
    ) {
        log.info("General error at route: {}", req.getRequestURL());
        log.info("General error message: {}", ex.getMessage());

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
                                                        .status(status.value())
                                                        .message("Unknown error occurred")
                                                        .data(null)
                                                        .build();

        return ResponseEntity.status(status).body(response);
    }
}
