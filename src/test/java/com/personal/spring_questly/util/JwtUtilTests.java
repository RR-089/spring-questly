package com.personal.spring_questly.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class JwtUtilTests {

    private final String testSecret = Base64.getEncoder().encodeToString(
            "thisisrandomstringfortestonlyonlyonlyonly".getBytes());

    private final long testExpiration = 1000000;

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setup() {
        jwtUtil = new JwtUtil();
        injectField(jwtUtil, "secret", testSecret);
        injectField(jwtUtil, "expirationMs", testExpiration);
    }

    @Test
    void testGenerateTokenAndExtractEmail() {
        String email = "test123@gmail.com";
        String token = jwtUtil.generateToken(email);
        String actualEmail = jwtUtil.extractEmail(token);


        assertNotNull(token);
        assertNotNull(actualEmail);
        assertEquals(email, actualEmail);
    }

    private void injectField(Object target, String field, Object value) {
        try {
            var declaredField = target.getClass().getDeclaredField(field);
            declaredField.setAccessible(true);
            declaredField.set(target, value);
        } catch (Exception ex) {
            throw new RuntimeException("Cannot inject field");
        }
    }
}
