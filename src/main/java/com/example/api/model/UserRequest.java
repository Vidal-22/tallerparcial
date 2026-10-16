package com.example.api.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Cuerpo de la petición para crear o actualizar un usuario ({@code POST} y {@code PUT /api/usuarios}).
 * <p>
 * La contraseña es obligatoria al crear; al actualizar es opcional y, si se omite, se conserva la actual.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    /** Nombre del usuario. */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
    private String nombre;

    /** Correo electrónico único. */
    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no tiene un formato válido")
    @Size(max = 120, message = "El correo no puede superar los 120 caracteres")
    private String correo;

    /** Contraseña en texto plano; solo se recibe, se guarda con hash y nunca se devuelve. */
    @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres")
    @ToString.Exclude
    private String contrasena;

    /** Rol del usuario. */
    @Size(max = 30, message = "El rol no puede superar los 30 caracteres")
    private String rol;
}
