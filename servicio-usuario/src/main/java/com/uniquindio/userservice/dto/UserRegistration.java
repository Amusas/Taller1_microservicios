package com.uniquindio.userservice.dto;

import jakarta.validation.constraints.*;

/**
 * DTO utilizado para registrar un nuevo usuario en el sistema.
 * Contiene los datos necesarios para crear la cuenta y su ubicación.
 */
public record UserRegistration(
        @Email(message = "El email debe tener un formato correcto")
        @Size(min = 8, max = 50, message = "El email debe contener entre 8 y 50 carácteres")
        @NotBlank(message = "El email es obligatorio")
        String email,

        @Size(min = 8, max = 50, message = "La contraseña debe contener entre 8 y 50 carácteres")
        @NotBlank(message = "La contraseña es obligatoria")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*$",
                message = "La contraseña debe contener al menos un dígito, una mayúscula y una minúscula")
        String password,

        @Size(min = 8, max = 50, message = "La contraseña debe contener entre 8 y 50 carácteres")
        @NotBlank(message = "El nombre es obligatorio")
        String name
) {}
