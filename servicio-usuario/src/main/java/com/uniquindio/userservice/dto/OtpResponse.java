package com.uniquindio.userservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.sql.Timestamp;

public record OtpResponse(
        int id,

        @NotNull(message = "El otp es obligatorio")
        @Size(min = 6, max = 6, message = "El otp debe tener 6 dígitos")
        String otp,

        @NotNull(message = "El ID de usuario es obligatorio")
        int user_id,

        @NotNull(message = "La fecha de creación es obligatoria")
        Timestamp created_at,

        @NotNull(message = "El estado del OTP es obligatorio")
        String otp_status
){
}
