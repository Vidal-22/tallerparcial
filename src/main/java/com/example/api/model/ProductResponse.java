package com.example.api.model;

/**
 * Representación de un producto que la API devuelve al cliente.
 * <p>
 * Es un {@code record}: Java ya genera el constructor, los accesores, {@code equals},
 * {@code hashCode} y {@code toString}, por lo que no necesita Lombok.
 *
 * @param id          identificador del producto
 * @param nombre      nombre del producto
 * @param descripcion descripción del producto
 * @param precio      precio unitario
 * @param stock       unidades disponibles
 */
public record ProductResponse(Long id, String nombre, String descripcion, Double precio, Integer stock) {

    /**
     * Convierte una entidad {@link Product} en su representación de respuesta.
     *
     * @param p entidad a convertir
     * @return el DTO de respuesta con los datos del producto
     */
    public static ProductResponse from(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getStock()
        );
    }
}
