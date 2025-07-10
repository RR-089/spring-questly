package com.personal.spring_questly.service;

import com.personal.spring_questly.exception.CustomException.NotFoundException;
import com.personal.spring_questly.model.User;
import com.personal.spring_questly.repository.UserRepository;
import com.personal.spring_questly.service.impl.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceTests {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;


    @Test
    void testLoadUserByUsername_NotFound() {
        String email = "test123@gmail.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        NotFoundException errors = assertThrows(NotFoundException.class, () ->
                userDetailsService.loadUserByUsername(email));

        assertNotNull(errors);
        assertEquals("User not found", errors.getMessage());
    }

    @Test
    void testLoadUserByUsername_Quester_Success() {
        String email = "test123@gmail.com";

        User foundUser = User.builder()
                             .id(UUID.randomUUID())
                             .email(email)
                             .firstName("Test")
                             .lastName("Tset")
                             .fullName("Test Tset")
                             .isQuester(true)
                             .isRequester(false)
                             .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(foundUser));

        UserDetails result = userDetailsService.loadUserByUsername(email);

        List<String> authorities = result.getAuthorities()
                                         .stream()
                                         .map(GrantedAuthority::getAuthority)
                                         .toList();

        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals(1, authorities.size());
        assertEquals("QUESTER", authorities.get(0));

        verify(userRepository, times(1)).findByEmail(email);

    }

    @Test
    void testLoadUserByUsername_Requester_Success() {
        String email = "test123@gmail.com";

        User foundUser = User.builder()
                             .id(UUID.randomUUID())
                             .email(email)
                             .firstName("Test")
                             .lastName("Tset")
                             .fullName("Test Tset")
                             .isQuester(false)
                             .isRequester(true)
                             .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(foundUser));

        UserDetails result = userDetailsService.loadUserByUsername(email);

        List<String> authorities = result.getAuthorities()
                                         .stream()
                                         .map(GrantedAuthority::getAuthority)
                                         .toList();

        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals(1, authorities.size());
        assertEquals("REQUESTER", authorities.get(0));

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void testLoadUserByUsername_QuesterAndRequester_Success() {
        String email = "test123@gmail.com";

        User foundUser = User.builder()
                             .id(UUID.randomUUID())
                             .email(email)
                             .firstName("Test")
                             .lastName("Tset")
                             .fullName("Test Tset")
                             .isQuester(true)
                             .isRequester(true)
                             .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(foundUser));

        UserDetails result = userDetailsService.loadUserByUsername(email);

        List<String> authorities = result.getAuthorities()
                                         .stream()
                                         .map(GrantedAuthority::getAuthority)
                                         .toList();

        assertNotNull(result);
        assertEquals(email, result.getUsername());
        assertEquals(2, authorities.size());

        authorities.forEach(auth -> {
            assertTrue(auth.equals("QUESTER") || auth.equals("REQUESTER"));
        });

        verify(userRepository, times(1)).findByEmail(email);
    }


}
