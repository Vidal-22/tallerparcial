package com.example.api.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entidad JPA que representa un producto de la tienda, persistida en la tabla {@code products}.
 * <p>
 * Se usan {@code @Getter} y {@code @Setter} en lugar de {@code @Data} para no generar
 * {@code equals}/{@code hashCode} sobre todos los campos, lo que causa problemas con los proxies de Hibernate.
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    /** Identificador autogenerado por la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre del producto (obligatorio, máximo 100 caracteres). */
    @Column(nullable = false, length = 100)
    private String name;

    /** Descripción opcional del producto. */
    @Column(length = 255)
    private String description;

    /** Precio unitario (obligatorio). */
    @Column(nullable = false)
    private Double price;

    /** Unidades disponibles en inventario. */
    private Integer stock;
}
