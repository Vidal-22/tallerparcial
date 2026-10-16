package com.example.api.service;

import com.example.api.exception.DuplicateResourceException;
import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.User;
import com.example.api.model.UserRequest;
import com.example.api.model.UserResponse;
import com.example.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Lógica de negocio del módulo de usuarios: correo único y contraseña guardada con hash BCrypt.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Lista todos los usuarios registrados, sin contraseñas.
     *
     * @return lista de usuarios (vacía si no hay registros)
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(UserResponse::from)
                .toList();
    }

    /**
     * Consulta un usuario por su identificador.
     *
     * @param id identificador del usuario
     * @return el usuario encontrado, sin contraseña
     * @throws ResourceNotFoundException si no existe un usuario con ese id
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return UserResponse.from(findOrThrow(id));
    }

    /**
     * Crea un usuario nuevo.
     *
     * @param request datos del usuario; la contraseña es obligatoria
     * @return el usuario creado, sin contraseña
     * @throws DuplicateResourceException si el correo ya está registrado
     * @throws IllegalArgumentException   si no se envía la contraseña
     */
    @Transactional
    public UserResponse createUser(UserRequest request) {
        String email = normalizeEmail(request.getCorreo());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Ya existe un usuario con el correo " + email);
        }
        if (request.getContrasena() == null || request.getContrasena().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }
        log.info("Creando usuario: {}", email);
        User user = new User();
        applyChanges(user, request, email);
        return UserResponse.from(userRepository.save(user));
    }

    /**
     * Actualiza un usuario existente. Si no se envía contraseña, se conserva la actual.
     *
     * @param id      identificador del usuario
     * @param request nuevos datos del usuario
     * @return el usuario actualizado, sin contraseña
     * @throws ResourceNotFoundException  si no existe un usuario con ese id
     * @throws DuplicateResourceException si el correo pertenece a otro usuario
     */
    @Transactional
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = findOrThrow(id);
        String email = normalizeEmail(request.getCorreo());
        if (userRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateResourceException("Ya existe un usuario con el correo " + email);
        }
        applyChanges(user, request, email);
        log.info("Actualizando usuario {}", id);
        return UserResponse.from(userRepository.save(user));
    }

    /**
     * Elimina un usuario existente.
     *
     * @param id identificador del usuario
     * @throws ResourceNotFoundException si no existe un usuario con ese id
     */
    @Transactional
    public void deleteUser(Long id) {
        userRepository.delete(findOrThrow(id));
        log.info("Usuario {} eliminado", id);
    }

    private User findOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + id));
    }

    private void applyChanges(User user, UserRequest request, String email) {
        user.setName(request.getNombre().trim());
        user.setEmail(email);
        user.setRole(request.getRol());
        if (request.getContrasena() != null && !request.getContrasena().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getContrasena()));
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
