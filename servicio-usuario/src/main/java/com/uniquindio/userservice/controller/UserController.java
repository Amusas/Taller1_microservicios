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

/**
 * Controlador REST para la gestión de usuarios del sistema.
 * 
 * <p>Esta clase expone endpoints HTTP para realizar operaciones CRUD (Create, Read, Update, Delete)
 * sobre usuarios. Implementa las mejores prácticas RESTful incluyendo códigos de estado HTTP
 * apropiados, headers de ubicación para recursos creados, y validación de entrada.</p>
 * 
 * <p>El controlador utiliza anotaciones de validación de Jakarta para asegurar la integridad
 * de los datos de entrada y proporciona logging detallado para auditoría y debugging.</p>
 * 
 * <p><strong>Características principales:</strong></p>
 * <ul>
 *   <li>Validación automática de DTOs de entrada</li>
 *   <li>Respuestas HTTP estándar con códigos de estado apropiados</li>
 *   <li>Headers de ubicación para recursos creados/actualizados</li>
 *   <li>Logging estructurado con emojis para mejor legibilidad</li>
 *   <li>Paginación para listas de usuarios</li>
 * </ul>
 * 
 * <p><strong>Seguridad:</strong> Algunos endpoints pueden requerir autenticación y autorización
 * dependiendo de la implementación del servicio subyacente.</p>
 * 
 * @author Andres Felipe Rendon
 * @version 1.0.0
 * @see UserService
 * @see UserRegistration
 * @see UserResponse
 * @see UserUpdateRequest
 * @see PaginatedUserResponse
 * @see ResponseEntity
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Slf4j
public class UserController {

    /**
     * Servicio de negocio para la gestión de usuarios.
     */
    private final UserService userService;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * <p>Este endpoint recibe un objeto {@link UserRegistration}, lo valida y lo
     * pasa al servicio para crear un nuevo usuario. Si la operación es exitosa,
     * devuelve el recurso creado junto con la ubicación en el header <code>Location</code>.</p>
     * 
      * <p><strong>Flujo de la operación:</strong></p>
 * <ol>
 *   <li>Validación automática del DTO de entrada</li>
 *   <li>Delegación al servicio de negocio</li>
 *   <li>Construcción de la URI de ubicación</li>
 *   <li>Respuesta con código 201 (Created) y header Location</li>
 * </ol>
     * 
     * <p><strong>Validaciones:</strong> El DTO se valida automáticamente usando las
     * anotaciones de validación de Jakarta definidas en {@link UserRegistration}.</p>
     *
     * @param userRegistration DTO con la información del nuevo usuario a registrar
     * @return {@link ResponseEntity} que contiene el usuario creado y el header Location
     *         con la URI del recurso creado
     * @throws jakarta.validation.ConstraintViolationException si la validación del DTO falla
     * @see UserRegistration
     * @see UserResponse
     * @see ResponseEntity#created(URI)
     */
    @PostMapping
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
     * Recupera una lista paginada de usuarios del sistema.
     * 
     * <p>Este endpoint permite consultar usuarios de forma paginada, lo que es especialmente
     * útil para aplicaciones que manejan grandes volúmenes de usuarios. La paginación
     * comienza desde la página 1 y permite configurar el tamaño de página.</p>
     * 
      * <p><strong>Parámetros de paginación:</strong></p>
 * <ul>
 *   <li><strong>page:</strong> Número de página (comienza en 1, no en 0)</li>
 *   <li><strong>size:</strong> Tamaño de la página (número de elementos por página)</li>
 * </ul>
     * 
     * <p><strong>Validaciones:</strong> Ambos parámetros deben ser números positivos.
     * Si no se proporcionan, se utilizan valores por defecto (página 1, tamaño 10).</p>
     * 
     * <p><strong>Nota de seguridad:</strong> Este endpoint puede requerir permisos de
     * administrador dependiendo de la implementación del servicio.</p>
     *
     * @param page Número de página a consultar (mínimo 1, por defecto 1)
     * @param size Tamaño de la página (mínimo 1 y máximo 100, por defecto 10)
     * @return {@link ResponseEntity} que contiene la lista paginada de usuarios
     * @throws jakarta.validation.ConstraintViolationException si los parámetros de paginación
     *         no son números positivos
     * @see PaginatedUserResponse
     * @see ResponseEntity#ok(Object)
     */
    @GetMapping
    public ResponseEntity<PaginatedUserResponse> getUsers(
            @RequestParam(defaultValue = "1") @Positive(message = "La página debe ser un número positivo") int page,
            @RequestParam(defaultValue = "10") @Positive(message = "El tamaño debe ser un número positivo") int size) {

        log.info("📋 Consultando usuarios - Página: {}, Tamaño: {}", page, size);
        PaginatedUserResponse response = userService.getUsers(page, size);
        log.info("✅ Total de usuarios recuperados: {}", response.totalItems());
        return ResponseEntity.ok(response);
    }

    /**
     * Consulta la información detallada de un usuario específico por su identificador único.
     * 
     * <p>Este endpoint permite obtener la información completa de un usuario específico
     * utilizando su ID. Es útil para mostrar perfiles de usuario, verificar información
     * antes de actualizaciones, o consultar datos para operaciones administrativas.</p>
     * 
     * <p><strong>Seguridad y autorización:</strong> Este endpoint puede requerir que el
     * usuario autenticado sea el propietario de la cuenta o tenga permisos de administrador,
     * dependiendo de la implementación del servicio subyacente.</p>
     * 
     * <p><strong>Manejo de errores:</strong> Si el usuario no existe, el servicio
     * puede lanzar una excepción que será manejada por el controlador de excepciones global.</p>
     *
     * @param userId Identificador único del usuario a consultar
     * @return {@link ResponseEntity} que contiene la información del usuario solicitado
     * @throws RuntimeException si el usuario no existe o si ocurre un error en el servicio
     * @see UserResponse
     * @see ResponseEntity#ok(Object)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable int userId) {
        log.info("🔎 Consultando usuario con ID: {}", userId);
        UserResponse response = userService.getUser(userId);
        log.info("✅ Usuario encontrado: {}", response.email());
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza la información personal de un usuario existente en el sistema.
     * 
     * <p>Este endpoint permite modificar los datos de un usuario existente. La operación
     * es idempotente y solo actualiza los campos proporcionados en el DTO de actualización.
     * Si la operación es exitosa, se devuelve el usuario actualizado junto con un header
     * de ubicación que apunta al recurso actualizado.</p>
     * 
     * <p><strong>Validaciones:</strong> El DTO de actualización se valida automáticamente
     * usando las anotaciones de validación de Jakarta definidas en {@link UserUpdateRequest}.</p>
     * 
     * <p><strong>Seguridad:</strong> Este endpoint requiere que el usuario autenticado
     * sea el propietario de la cuenta o tenga permisos de administrador.</p>
     * 
     * <p><strong>Headers de respuesta:</strong> Se incluye el header <code>Location</code>
     * que apunta a la URI del recurso actualizado para facilitar futuras consultas.</p>
     *
     * @param id Identificador único del usuario a actualizar
     * @param userUpdateRequest DTO con los datos actualizados del usuario
     * @return {@link ResponseEntity} que contiene el usuario actualizado y el header Location
     * @throws jakarta.validation.ConstraintViolationException si la validación del DTO falla
     * @throws RuntimeException si el usuario no existe o si ocurre un error en el servicio
     * @see UserUpdateRequest
     * @see UserResponse
     * @see ResponseEntity#ok()
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable int id,
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
     * Elimina lógicamente la cuenta de un usuario del sistema.
     * 
     * <p>Este endpoint realiza una eliminación lógica del usuario, lo que significa que
     * los datos no se eliminan físicamente de la base de datos, sino que se marcan como
     * inactivos o eliminados. Esto permite la recuperación de cuentas si es necesario.</p>
     * 
     * <p><strong>Seguridad:</strong> Este endpoint puede requere que el usuario autenticado
     * sea el propietario de la cuenta o tenga permisos de administrador, ya que es una
     * operación sensible que afecta la disponibilidad de la cuenta.</p>
     * 
     * <p><strong>Respuesta:</strong> Si la operación es exitosa, se devuelve un código
     * de estado 204 (No Content) sin cuerpo de respuesta, indicando que la operación
     * se completó exitosamente.</p>
     * 
     * <p><strong>Advertencia:</strong> Esta operación puede ser irreversible dependiendo
     * de la implementación del servicio. Se recomienda confirmar la acción antes de
     * proceder con la eliminación.</p>
     *
     * @param id Identificador único del usuario a eliminar
     * @return {@link ResponseEntity} sin contenido (código 204) si la eliminación fue exitosa
     * @throws RuntimeException si el usuario no existe o si ocurre un error en el servicio
     * @see ResponseEntity#noContent()
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        log.info("🗑️ Eliminando usuario con ID: {}", id);
        userService.deleteUser(id);
        log.info("✅ Usuario con ID: {} eliminado correctamente", id);
        return ResponseEntity.noContent().build();
    }
}
