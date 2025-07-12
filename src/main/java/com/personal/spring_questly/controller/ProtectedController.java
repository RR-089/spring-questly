package com.personal.spring_questly.controller;

import com.personal.spring_questly.annotation.auth.QuesterOnly;
import com.personal.spring_questly.annotation.auth.RequesterOnly;
import com.personal.spring_questly.annotation.docs.protecteds.GetQuesterOnlyResponseDocs;
import com.personal.spring_questly.annotation.docs.protecteds.GetRequesterOnlyResponseDocs;
import com.personal.spring_questly.dto.common.ApiResponseDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/protected")
@Tag(name = "Protected", description = "Just examples of protected route")
@SecurityRequirement(name = "bearerAuth")
public class ProtectedController {

    @GetMapping(value = "quester")
    @QuesterOnly
    @GetQuesterOnlyResponseDocs
    public ResponseEntity<ApiResponseDTO<Object>> getOnlyQuesterRoute() {
        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
                                                        .status(HttpStatus.OK.value())
                                                        .message("Yes this is quester only route")
                                                        .data(this.getLoggedUserData())
                                                        .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "requester")
    @RequesterOnly
    @GetRequesterOnlyResponseDocs
    public ResponseEntity<ApiResponseDTO<Object>> getOnlyRequesterRoute() {
        ApiResponseDTO<Object> response = ApiResponseDTO.builder()
                                                        .status(HttpStatus.OK.value())
                                                        .message("Yes this is requester" +
                                                                " only route")
                                                        .data(this.getLoggedUserData())
                                                        .build();

        return ResponseEntity.ok(response);
    }


    private UserDetails getLoggedUserDetails() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return (UserDetails) authentication.getPrincipal();
    }

    private Object getLoggedUserData() {
        UserDetails userDetails = this.getLoggedUserDetails();

        Map<String, Object> loggedUserData = new HashMap<>();

        loggedUserData.put("email", userDetails.getUsername());
        loggedUserData.put("authorities",
                userDetails.getAuthorities().stream().map(Object::toString).toList());

        return loggedUserData;
    }

}
