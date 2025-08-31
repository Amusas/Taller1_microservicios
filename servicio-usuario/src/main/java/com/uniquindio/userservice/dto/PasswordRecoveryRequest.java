package com.uniquindio.userservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordRecoveryRequest (
        @Email(message = "El email debe tener un formato correcto")
        @Size(min = 8, max = 50, message = "El email tener un formato correcto")
        @NotBlank(message = "El email es obligatorio")
        String email,

        @Size(min = 6, max = 6, message = "El OTP debe tener 6 dígitos")
        @NotBlank(message = "El OTP es obligatorio")
        int otp,

        @Size(min = 8, max = 50, message = "La contraseña debe contener entre 8 y 50 carácteres")
        @NotBlank(message = "La contraseña es obligatoria")
        @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).*$",
                message = "La contraseña debe contener al menos un dígito, una mayúscula y una minúscula")
        String password
){
}
