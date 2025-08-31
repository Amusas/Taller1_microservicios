package com.uniquindio.userservice.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.sql.Timestamp;

public record OtpRegistration(

        @NotNull(message = "El ID de usuario es obligatorio")
        int user_id
) {
}