package com.example.api.service;

import com.example.api.exception.ResourceNotFoundException;
import com.example.api.model.Product;
import com.example.api.model.ProductRequest;
import com.example.api.model.ProductResponse;
import com.example.api.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio del módulo de productos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * Lista todos los productos registrados.
     *
     * @return lista de productos (vacía si no hay registros)
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    /**
     * Consulta un producto por su identificador.
     *
     * @param id identificador del producto
     * @return el producto encontrado
     * @throws ResourceNotFoundException si no existe un producto con ese id
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return ProductResponse.from(findOrThrow(id));
    }

    /**
     * Crea un producto nuevo.
     *
     * @param request datos del producto
     * @return el producto creado, con su id asignado
     */
    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creando producto: {}", request.getNombre());
        Product product = new Product();
        applyChanges(product, request);
        return ProductResponse.from(productRepository.save(product));
    }

    /**
     * Actualiza todos los datos de un producto existente.
     *
     * @param id      identificador del producto
     * @param request nuevos datos del producto
     * @return el producto actualizado
     * @throws ResourceNotFoundException si no existe un producto con ese id
     */
    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findOrThrow(id);
        applyChanges(product, request);
        log.info("Actualizando producto {}", id);
        return ProductResponse.from(productRepository.save(product));
    }

    /**
     * Elimina un producto existente.
     *
     * @param id identificador del producto
     * @throws ResourceNotFoundException si no existe un producto con ese id
     */
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.delete(findOrThrow(id));
        log.info("Producto {} eliminado", id);
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
    }

    private void applyChanges(Product product, ProductRequest request) {
        product.setName(request.getNombre().trim());
        product.setDescription(request.getDescripcion());
        product.setPrice(request.getPrecio());
        product.setStock(request.getStock());
    }
}
