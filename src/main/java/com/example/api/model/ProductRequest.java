package com.example.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cuerpo de la petición para crear o actualizar un producto ({@code POST} y {@code PUT /api/productos}).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    /** Nombre del producto. */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    /** Descripción opcional del producto. */
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcion;

    /** Precio unitario; debe ser mayor que cero. */
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que cero")
    private Double precio;

    /** Unidades en inventario; no puede ser negativo. */
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;
}
