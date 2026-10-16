package com.example.api.repository;

import com.example.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio Spring Data JPA para la entidad {@link User}.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Indica si ya existe un usuario con el correo dado, sin distinguir mayúsculas.
     *
     * @param email correo a comprobar
     * @return {@code true} si el correo ya está registrado
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Indica si el correo está registrado por un usuario distinto al indicado. Se usa al actualizar,
     * para que un usuario pueda conservar su propio correo.
     *
     * @param email correo a comprobar
     * @param id    identificador del usuario que se está actualizando
     * @return {@code true} si otro usuario ya usa ese correo
     */
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
