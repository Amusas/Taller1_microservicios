package com.uniquindio.userservice.service.impl;

import com.uniquindio.userservice.client.UserClient;
import com.uniquindio.userservice.dto.LoginRequest;
import com.uniquindio.userservice.dto.UserAuthResponse;
import com.uniquindio.userservice.exception.ExternalServiceException;
import com.uniquindio.userservice.exception.IncorrectPasswordException;
import com.uniquindio.userservice.exception.UserNotFoundException;
import com.uniquindio.userservice.service.interfaces.AuthService;
import com.uniquindio.userservice.util.JwtUtils;
import com.uniquindio.userservice.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserClient userClient;
    private final JwtUtils jwtUtils;


    @Override
    public String login(LoginRequest loginRequest) {
        log.info("Intentando iniciar sesión para el usuario con email: {}", loginRequest.email());

        try {
            // Buscar usuario en el user-service
            UserAuthResponse user = userClient.getUserByEmail(loginRequest.email());

            // Validar contraseña en este microservicio
            if (!PasswordUtils.matches(loginRequest.password(), user.password())) {
                log.error("Contraseña incorrecta para el usuario {}", loginRequest.email());
                throw new IncorrectPasswordException("Contraseña incorrecta para el usuario " + loginRequest.email());
            }

            // Generar token JWT
            String token = jwtUtils.generateToken(user.email());
            log.info("Token JWT generado exitosamente para el usuario {}", loginRequest.email());

            return token;

        } catch (WebClientResponseException e) {
            log.error("Error al obtener usuario. Código: {}, Detalle: {}", e.getStatusCode(), e.getResponseBodyAsString());

            if (e.getStatusCode().value() == 404) {
                throw new UserNotFoundException("Usuario con email " + loginRequest.email() + " no encontrado");
            } else {
                throw new ExternalServiceException(
                        "Error al comunicarse con el servicio de usuarios: " + e.getResponseBodyAsString()
                );
            }
        }
    }

}
