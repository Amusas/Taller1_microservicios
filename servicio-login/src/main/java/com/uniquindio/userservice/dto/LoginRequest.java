package product.service.servicio_login.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest (
        @NotBlank(message = "El nombre es obligatorio")
        String name,
        @NotBlank(message = "La clave es obligatoria")
        String password
){
}
