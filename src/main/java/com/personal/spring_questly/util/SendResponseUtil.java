package com.personal.spring_questly.util;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.spring_questly.dto.common.ApiResponseDTO;
import com.personal.spring_questly.exception.CustomException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;

public class SendResponseUtil {
    public static void sendCustomErrorResponse(CustomException ex,
                                               HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(ex.getStatus().value());

        String json = new ObjectMapper().writeValueAsString(
                ApiResponseDTO.builder()
                              .status(ex.getStatus().value())
                              .message(ex.getMessage())
                              .data(ex.getData())
                              .build()
        );

        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
