package com.uniquindio.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta que representa a un usuario del sistema.
 *
 * Contiene la información básica de identificación del usuario.
 */
@Schema(description = "Respuesta con la información básica de un usuario")
public record UserResponse(

        @Schema(
                description = "Identificador único del usuario",
                example = "101"
        )
        int id,

        @Schema(
                description = "Nombre completo del usuario",
                example = "Andrés Felipe Rendón"
        )
        String name,

        @Schema(
                description = "Correo electrónico del usuario",
                example = "usuario@ejemplo.com"
        )
        String email
) {
}
