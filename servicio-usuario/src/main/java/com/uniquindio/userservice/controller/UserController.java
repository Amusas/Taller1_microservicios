package com.uniquindio.userservice.controller;

import com.uniquindio.userservice.dto.PaginatedUserResponse;
import com.uniquindio.userservice.dto.UserRegistration;
import com.uniquindio.userservice.dto.UserResponse;
import com.uniquindio.userservice.dto.UserUpdateRequest;
import com.uniquindio.userservice.service.interfaces.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * <p>Este endpoint recibe un objeto {@link UserRegistration}, lo valida y lo
     * pasa al servicio para crear un nuevo usuario. Si la operación es exitosa,
     * devuelve el recurso creado junto con la ubicación en el header <code>Location</code>.</p>
     *
     * @param userRegistration DTO con la información del nuevo usuario
     * @return {@link ResponseEntity} que contiene el usuario creado y el header Location
     */
    @PostMapping("/users")
    public ResponseEntity<UserResponse> registerUser(@RequestBody @Valid UserRegistration userRegistration) {
        log.info("Solicitud recibida para registrar usuario: {}", userRegistration.email());

        UserResponse userResponse = userService.registerUser(userRegistration);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userResponse.id())
                .toUri();

        log.info("Usuario registrado exitosamente: id={}, email={}", userResponse.id(), userResponse.email());
        return ResponseEntity.created(location).body(userResponse);
    }


    /**
     * Recupera una lista paginada de usuarios.
     * Solo accesible por administradores.
     *
     * @param page Número de página (mínimo 1).
     * @param size Tamaño de la página (mínimo 1 y máximo 100).
     * @return Lista paginada de usuarios.
     */
    @GetMapping
    public ResponseEntity<PaginatedUserResponse> getUsers(
            @RequestParam(defaultValue = "1") @Positive(message = "La página debe ser un número positivo") int page,
            @RequestParam(defaultValue = "30") @Positive(message = "El tamaño debe ser un número positivo") int size) {

        log.info("📋 Consultando usuarios - Página: {}, Tamaño: {}", page, size);
        PaginatedUserResponse response = userService.getUsers(page, size);
        log.info("✅ Total de usuarios recuperados: {}", response.totalItems());
        return ResponseEntity.ok(response);
    }


    /**
     * Consulta la información de un usuario por su ID.
     * Permitido al propio usuario o a administradores.
     *
     * @param userId ID del usuario.
     * @return Datos del usuario.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable int userId) {
        log.info("🔎 Consultando usuario con ID: {}", userId);
        UserResponse response = userService.getUser(userId);
        log.info("✅ Usuario encontrado: {}", response.email());
        return ResponseEntity.ok(response);
    }


    /**
     * Actualiza los datos personales del usuario.
     *
     * @param id                ID del usuario.
     * @param userUpdateRequest Datos nuevos.
     * @return Usuario actualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest) {

        log.info("✏️ Actualizando usuario con ID: {}", id);
        UserResponse response = userService.updateUser(id, userUpdateRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .build()
                .toUri();

        log.info("✅ Usuario actualizado: {}", response.email());
        return ResponseEntity.ok()
                .header(HttpHeaders.LOCATION, location.toString())
                .body(response);
    }


    /**
     * Elimina lógicamente la cuenta de un usuario.
     *
     * @param id ID del usuario.
     * @return Sin contenido si fue exitoso.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.info("🗑️ Eliminando usuario con ID: {}", id);
        userService.deleteUser(id);
        log.info("✅ Usuario con ID: {} eliminado correctamente", id);
        return ResponseEntity.noContent().build();
    }


}
