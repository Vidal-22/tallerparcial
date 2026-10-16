package com.example.api.controller;

import com.example.api.model.ProductRequest;
import com.example.api.model.ProductResponse;
import com.example.api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controlador REST que expone el CRUD de productos en {@code /api/productos}.
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Lista todos los productos.
     *
     * @return 200 con la lista de productos
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }

    /**
     * Consulta un producto por su identificador.
     *
     * @param id identificador del producto
     * @return 200 con el producto, o 404 si no existe
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * Crea un producto.
     *
     * @param request datos del producto en el cuerpo JSON
     * @return 201 con el producto creado y la cabecera {@code Location}, o 400 si los datos son inválidos
     */
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        ProductResponse created = productService.createProduct(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Actualiza un producto existente.
     *
     * @param id      identificador del producto
     * @param request nuevos datos del producto
     * @return 200 con el producto actualizado, 404 si no existe o 400 si los datos son inválidos
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id,
                                                         @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    /**
     * Elimina un producto.
     *
     * @param id identificador del producto
     * @return 204 sin contenido, o 404 si no existe
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
