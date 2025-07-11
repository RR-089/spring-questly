package com.personal.spring_questly.service;

import com.personal.spring_questly.dto.auth.LoginDTO;
import com.personal.spring_questly.dto.auth.RegisterDTO;

public interface AuthService {
    String register(RegisterDTO dto);

    String login(LoginDTO dto);
}
