package com.uniquindio.userservice.service.impl;

import com.uniquindio.userservice.dto.LoginRequest;
import com.uniquindio.userservice.service.interfaces.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public String login(LoginRequest loginRequest) {
        return "";
    }

}
