package com.uniquindio.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OtpRequest(
        @Email(message = "El email debe tener un formato correcto")
        @Size(min = 8, max = 50, message = "El email debe tener un formato correcto")
        @NotBlank(message = "El email es obligatorio")
        String email
) {
}
