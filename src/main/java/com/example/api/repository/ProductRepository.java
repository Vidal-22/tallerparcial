package com.example.api.repository;


import com.example.api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repositorio Spring Data JPA para la entidad {@link Product}.
 * Hereda de {@link JpaRepository} las operaciones CRUD básicas.
 */
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Indica si existe un producto con el nombre dado, sin distinguir mayúsculas.
     *
     * @param name nombre a buscar
     * @return {@code true} si existe al menos un producto con ese nombre
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Busca productos cuyo nombre contenga el texto dado, sin distinguir mayúsculas.
     *
     * @param name fragmento del nombre
     * @return lista de productos coincidentes (vacía si no hay ninguno)
     */
    List<Product> findByNameContainingIgnoreCase(String name);
}
