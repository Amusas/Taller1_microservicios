package com.uniquindio.userservice.service.interfaces;

import com.uniquindio.userservice.dto.LoginRequest;

public interface AuthService {
    String login(LoginRequest loginRequest);
}
