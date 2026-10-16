package com.example.api.controller;

import com.example.api.model.UserRequest;
import com.example.api.model.UserResponse;
import com.example.api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST que expone el CRUD de usuarios en {@code /api/usuarios}.
 * Ninguna respuesta incluye la contraseña.
 */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Lista todos los usuarios.
     *
     * @return 200 con la lista de usuarios
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    /**
     * Consulta un usuario por su identificador.
     *
     * @param id identificador del usuario
     * @return 200 con el usuario, o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Crea un usuario.
     *
     * @param request datos del usuario en el cuerpo JSON
     * @return 201 con el usuario creado, o 400 si el correo está duplicado o los datos son inválidos
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse created = userService.createUser(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @param id      identificador del usuario
     * @param request nuevos datos del usuario
     * @return 200 con el usuario actualizado, 404 si no existe o 400 si el correo pertenece a otro usuario
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * Elimina un usuario.
     *
     * @param id identificador del usuario
     * @return 204 sin contenido, o 404 si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
