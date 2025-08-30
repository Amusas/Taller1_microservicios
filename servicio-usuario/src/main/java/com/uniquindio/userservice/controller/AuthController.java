package com.uniquindio.userservice.controller;

import com.uniquindio.userservice.dto.LoginRequest;
import com.uniquindio.userservice.service.interfaces.AuthService;
import com.uniquindio.userservice.util.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest loginRequest){
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    /**
     * Solicita un código de recuperación de contraseña para el correo electrónico dado.
     *
     * @param email Correo del usuario que desea recuperar su contraseña.
     * @return HTTP 204 si el correo fue enviado correctamente.
     */
    @PostMapping("/passwordCodes")
    public ResponseEntity<Void> requestPasswordReset(@RequestParam String email) {
        log.info("🔁 Solicitud de recuperación de contraseña para: {}", email);
        authService.sendPasswordResetCode(email);
        return ResponseEntity.noContent().build();
    }


}
