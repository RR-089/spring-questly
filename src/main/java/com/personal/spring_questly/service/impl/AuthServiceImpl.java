package com.personal.spring_questly.service.impl;

import com.personal.spring_questly.dto.auth.LoginDTO;
import com.personal.spring_questly.dto.auth.RegisterDTO;
import com.personal.spring_questly.exception.CustomException.BadRequestException;
import com.personal.spring_questly.exception.CustomException.UnauthorizedException;
import com.personal.spring_questly.model.User;
import com.personal.spring_questly.repository.UserRepository;
import com.personal.spring_questly.service.AuthService;
import com.personal.spring_questly.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String register(RegisterDTO dto) {
        if (userRepository.existsByEmail(dto.email())) {
            throw new BadRequestException("Email is already taken", null);
        }

        User newUser = User.builder()
                           .email(dto.email())
                           .password(passwordEncoder.encode(dto.password()))
                           .firstName(dto.firstName())
                           .lastName(dto.lastName())
                           .fullName(dto.firstName() + " " + dto.lastName())
                           .isQuester(dto.isQuester())
                           .isRequester(dto.isRequester())
                           .build();

        return jwtUtil.generateToken(userRepository.save(newUser).getEmail());
    }

    @Override
    public String login(LoginDTO dto) {
        User foundUser = userRepository.findByEmail(dto.email()).orElseThrow(
                () -> new UnauthorizedException("Invalid credentials", null)
        );

        if (!passwordEncoder.matches(dto.password(), foundUser.getPassword())) {
            throw new UnauthorizedException("Invalid credentials", null);
        }

        return jwtUtil.generateToken(foundUser.getEmail());
    }
}
