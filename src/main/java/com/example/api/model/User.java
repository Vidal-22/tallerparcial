package com.example.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa un usuario, persistida en la tabla {@code users}.
 * <p>
 * La API nunca serializa esta entidad (responde con {@link UserResponse}); aun así el campo
 * {@code password} se marca como de solo escritura como medida adicional de seguridad.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** Identificador autogenerado por la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del usuario (obligatorio, máximo 80 caracteres). */
    @Column(nullable = false, length = 80)
    private String name;

    /** Correo electrónico, único en la tabla (máximo 120 caracteres). */
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    /** Hash BCrypt de la contraseña; nunca se guarda en texto plano. */
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /** Rol del usuario (por ejemplo, ADMIN o CLIENTE). */
    @Column(length = 30)
    private String role;
}
