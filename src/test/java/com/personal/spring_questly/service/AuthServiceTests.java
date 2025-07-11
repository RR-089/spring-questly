package com.personal.spring_questly.service;

import com.personal.spring_questly.dto.auth.LoginDTO;
import com.personal.spring_questly.dto.auth.RegisterDTO;
import com.personal.spring_questly.exception.CustomException.BadRequestException;
import com.personal.spring_questly.exception.CustomException.UnauthorizedException;
import com.personal.spring_questly.model.User;
import com.personal.spring_questly.repository.UserRepository;
import com.personal.spring_questly.service.impl.AuthServiceImpl;
import com.personal.spring_questly.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTests {
    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;


    @Test
    void testRegister_EmailIsAlreadyTaken_BadRequest() {
        RegisterDTO dto = RegisterDTO.builder()
                                     .email("test123@gmail.com")
                                     .password("123qwe")
                                     .firstName("Test")
                                     .lastName("Tset")
                                     .firstName("Test Tset")
                                     .isQuester(true)
                                     .isRequester(false)
                                     .build();

        when(userRepository.existsByEmail(dto.email())).thenReturn(true);

        BadRequestException errors = assertThrows(BadRequestException.class,
                () -> authService.register(dto));

        assertNotNull(errors);
        assertEquals("Email is already taken", errors.getMessage());

        verify(userRepository, times(1)).existsByEmail(dto.email());
        verify(userRepository, times(0)).save(any(User.class));
        verify(jwtUtil, times(0)).generateToken(dto.email());
        verify(passwordEncoder, times(0)).encode(dto.password());
    }


    @Test
    void testRegister_Success() {
        RegisterDTO dto = RegisterDTO.builder()
                                     .email("test123@gmail.com")
                                     .password("123qwe")
                                     .firstName("Test")
                                     .lastName("Tset")
                                     .firstName("Test Tset")
                                     .isQuester(true)
                                     .isRequester(false)
                                     .build();

        String fullName = dto.firstName() + " " + dto.lastName();

        User newUser = User.builder()
                           .email(dto.email())
                           .password("encoded")
                           .firstName(dto.firstName())
                           .lastName(dto.lastName())
                           .fullName(fullName)
                           .isQuester(dto.isQuester())
                           .isRequester(dto.isRequester())
                           .build();

        when(userRepository.existsByEmail(dto.email())).thenReturn(false);
        when(userRepository.save(newUser)).thenReturn(newUser);
        when(passwordEncoder.encode(dto.password())).thenReturn("encoded");
        when(jwtUtil.generateToken(newUser.getEmail())).thenReturn("jwt token");

        String result = authService.register(dto);

        assertNotNull(result);

        verify(userRepository, times(1)).existsByEmail(dto.email());
        verify(userRepository, times(1)).save(any(User.class));
        verify(jwtUtil, times(1)).generateToken(dto.email());
        verify(passwordEncoder, times(1)).encode(dto.password());
    }

    @Test
    void testLogin_UserNotFound_Unauthorized() {
        LoginDTO dto = LoginDTO.builder()
                               .email("test123@gmail.com")
                               .password("123qwe")
                               .build();

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.empty());

        UnauthorizedException errors = assertThrows(UnauthorizedException.class, () ->
                authService.login(dto));

        assertNotNull(errors);
        assertEquals(HttpStatus.UNAUTHORIZED, errors.getStatus());

        verify(userRepository, times(1)).findByEmail(dto.email());
    }

    @Test
    void testLogin_IncorrectPassword_Unauthorized() {
        LoginDTO dto = LoginDTO.builder()
                               .email("test123@gmail.com")
                               .password("123qwe")
                               .build();

        User foundUser = User.builder()
                             .id(UUID.randomUUID())
                             .email(dto.email())
                             .password("567tyu")
                             .firstName("Test")
                             .lastName("Tset")
                             .fullName("Test Tset")
                             .isQuester(true)
                             .isRequester(false)
                             .build();

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(foundUser));
        when(passwordEncoder.matches(dto.password(), foundUser.getPassword())).thenReturn(false);

        UnauthorizedException errors = assertThrows(UnauthorizedException.class, () ->
                authService.login(dto));

        assertNotNull(errors);
        assertEquals(HttpStatus.UNAUTHORIZED, errors.getStatus());
        verify(userRepository, times(1)).findByEmail(dto.email());
    }

    @Test
    void testLogin_Success() {
        LoginDTO dto = LoginDTO.builder()
                               .email("test123@gmail.com")
                               .password("123qwe")
                               .build();

        User foundUser = User.builder()
                             .id(UUID.randomUUID())
                             .email(dto.email())
                             .password("123qwe")
                             .firstName("Test")
                             .lastName("Tset")
                             .fullName("Test Tset")
                             .isQuester(true)
                             .isRequester(false)
                             .build();

        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(foundUser));
        when(passwordEncoder.matches(dto.password(), foundUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(foundUser.getEmail())).thenReturn("jwt token");

        String result = authService.login(dto);

        assertNotNull(result);
        verify(userRepository, times(1)).findByEmail(dto.email());
        verify(jwtUtil, times(1)).generateToken(foundUser.getEmail());
    }
}
