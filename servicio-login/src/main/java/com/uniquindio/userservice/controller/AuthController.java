package com.uniquindio.userservice.controller;

import com.uniquindio.userservice.dto.LoginRequest;
import com.uniquindio.userservice.util.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok(jwtUtils.generateToken(loginRequest));
    }


}
