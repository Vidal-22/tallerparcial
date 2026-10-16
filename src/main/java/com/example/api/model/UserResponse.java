package com.example.api.model;

/**
 * Representación de un usuario que la API devuelve al cliente. No incluye la contraseña.
 *
 * @param id     identificador del usuario
 * @param nombre nombre del usuario
 * @param correo correo electrónico
 * @param rol    rol del usuario
 */
public record UserResponse(Long id, String nombre, String correo, String rol) {

    /**
     * Convierte una entidad {@link User} en su representación de respuesta, omitiendo la contraseña.
     *
     * @param u entidad a convertir
     * @return el DTO de respuesta sin datos sensibles
     */
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole());
    }
}
