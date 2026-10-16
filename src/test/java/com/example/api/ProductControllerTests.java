package com.example.api;

import com.example.api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración del CRUD de productos (controlador + servicio + JPA sobre H2).
 */
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTests {

    private static final String BODY = """
            {"nombre":"Teclado mecánico","descripcion":"Switches rojos, retroiluminado","precio":189900,"stock":15}
            """;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void clean() {
        productRepository.deleteAll();
    }

    private long create(String body) throws Exception {
        MvcResult result = mvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", notNullValue()))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.nombre").value("Teclado mecánico"))
                .andReturn();
        String json = result.getResponse().getContentAsString();
        return Long.parseLong(json.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    @Test
    void fullCrudFlow() throws Exception {
        long id1 = create(BODY);
        long id2 = create(BODY);

        mvc.perform(get("/api/productos")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/api/productos/" + id1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.precio").value(189900.0))
                .andExpect(jsonPath("$.stock").value(15));

        mvc.perform(put("/api/productos/" + id1).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Teclado RGB\",\"descripcion\":\"Actualizado\",\"precio\":199900,\"stock\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Teclado RGB"))
                .andExpect(jsonPath("$.stock").value(10));

        mvc.perform(delete("/api/productos/" + id2))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
        mvc.perform(get("/api/productos")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void missingProductReturns404() throws Exception {
        mvc.perform(get("/api/productos/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Producto no encontrado con id 999"));
        mvc.perform(put("/api/productos/999").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/productos/999")).andExpect(status().isNotFound());
    }

    @Test
    void invalidBodyReturns400() throws Exception {
        mvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"precio\":-5}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Datos inválidos"))
                .andExpect(jsonPath("$.campos.nombre", notNullValue()))
                .andExpect(jsonPath("$.campos.precio", notNullValue()));
        mvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON).content("{malformado"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/api/productos/abc")).andExpect(status().isBadRequest());
    }
}
