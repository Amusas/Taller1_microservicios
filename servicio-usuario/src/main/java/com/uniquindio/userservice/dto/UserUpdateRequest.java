package com.uniquindio.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Email(message = "El email debe tener un formato correcto")
        @Size(min = 8, max = 50, message = "El email debe contener entre 8 y 50 carácteres")
        @NotBlank(message = "El email es obligatorio")
        String email,
        @Size(min = 8, max = 50, message = "La contraseña debe contener entre 8 y 50 carácteres")
        @NotBlank(message = "El nombre es obligatorio")
        String name
) {
}
