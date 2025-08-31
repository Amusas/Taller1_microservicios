package com.uniquindio.userservice.controller;

import com.uniquindio.userservice.dto.*;
import com.uniquindio.userservice.service.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la autenticación y gestión de credenciales.
 *
 * Endpoints:
 * - POST /api/v1/auth/login              : Inicio de sesión (retorna JWT)
 * - POST /api/v1/auth/otp-generator      : Solicita/genera OTP para un email
 * - POST /api/v1/auth/password-recovery  : Valida OTP y cambia la contraseña
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    /**
     * Inicia sesión y retorna el JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("🔐 Login solicitado para: {}", loginRequest.email());
        String token = authService.login(loginRequest);
        log.info("✅ Login exitoso para: {}", loginRequest.email());
        return ResponseEntity.ok(token);
    }

    /**
     * Genera/solicita un OTP para el email indicado.
     */
    @PostMapping("/otp-generator")
    public ResponseEntity<OtpResponse> requestOtp(@RequestBody @Valid OtpRequest request) {
        log.info("📩 Solicitud de OTP para: {}", request.email());
        OtpResponse otp = authService.requestOtp(request);
        log.info("✅ OTP generado para: {}", request.email());
        return ResponseEntity.ok(otp);
    }

    /**
     * Valida el OTP y actualiza la contraseña.
     */
    @PostMapping("/password-recovery")
    public ResponseEntity<Boolean> recoverPassword(@RequestBody @Valid PasswordRecoveryRequest request) {
        log.info("🔑 Recuperación de contraseña solicitada para: {}", request.email());
        Boolean response = authService.updatePassword(request);
        log.info("✅ Contraseña actualizada para: {}", request.email());
        return ResponseEntity.ok(response);
    }
}
