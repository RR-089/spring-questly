package com.personal.spring_questly.controller;

import com.personal.spring_questly.dto.common.ApiResponseDTO;
import com.personal.spring_questly.model.File;
import com.personal.spring_questly.model.User;
import com.personal.spring_questly.repository.UserRepository;
import com.personal.spring_questly.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;
    private final UserRepository userRepository;

    //TODO: Test route delete this later
    @PostMapping(value = "upload", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponseDTO<String>> uploadFile(@RequestPart("file") MultipartFile file) {
        List<File> data = fileService.bulkUploadFiles("user-profile", List.of(file));

        User foundUser = userRepository.findByEmail("deerut@gmail.com").orElseThrow();

        foundUser.getUserProfile().setProfilePicture(data.get(0));

        userRepository.save(foundUser);

        return ResponseEntity.ok(
                ApiResponseDTO.<String>builder()
                              .status(HttpStatus.OK.value())
                              .message("upload success")
                              .data(data.get(0).getName())
                              .build()
        );
    }
}
