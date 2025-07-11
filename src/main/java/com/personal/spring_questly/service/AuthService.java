package com.personal.spring_questly.service;

import com.personal.spring_questly.dto.auth.LoginRequestDTO;
import com.personal.spring_questly.dto.auth.RegisterRequestDTO;

public interface AuthService {
    String register(RegisterRequestDTO dto);

    String login(LoginRequestDTO dto);
}
