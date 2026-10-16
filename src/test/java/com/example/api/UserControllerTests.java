package com.example.api;

import com.example.api.model.User;
import com.example.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración del CRUD de usuarios y de sus reglas de negocio:
 * correo único y contraseña nunca expuesta.
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    private static String body(String nombre, String correo, String contrasena) {
        String pwd = contrasena == null ? "" : ",\"contrasena\":\"" + contrasena + "\"";
        return "{\"nombre\":\"" + nombre + "\",\"correo\":\"" + correo + "\",\"rol\":\"CLIENTE\"" + pwd + "}";
    }

    private long create(String nombre, String correo) throws Exception {
        String json = mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content(body(nombre, correo, "secreto123")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.correo").value(correo))
                .andExpect(jsonPath("$.contrasena").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andReturn().getResponse().getContentAsString();
        return Long.parseLong(json.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }

    @Test
    void fullCrudFlowNeverExposesPassword() throws Exception {
        long id1 = create("Ana", "ana@correo.com");
        long id2 = create("Luis", "luis@correo.com");

        mvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().string(not(containsString("contrasena"))))
                .andExpect(content().string(not(containsString("password"))));
        mvc.perform(get("/api/usuarios/" + id1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());

        // Actualizar conservando el propio correo no debe considerarse duplicado; sin contraseña se conserva la actual
        mvc.perform(put("/api/usuarios/" + id1).contentType(MediaType.APPLICATION_JSON)
                        .content(body("Ana María", "ana@correo.com", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana María"))
                .andExpect(jsonPath("$.contrasena").doesNotExist());
        User stored = userRepository.findById(id1).orElseThrow();
        assertThat(stored.getPassword()).isNotEqualTo("secreto123");
        assertThat(passwordEncoder.matches("secreto123", stored.getPassword())).isTrue();

        mvc.perform(delete("/api/usuarios/" + id2)).andExpect(status().isNoContent());
        mvc.perform(get("/api/usuarios")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void duplicateEmailReturns400() throws Exception {
        long id1 = create("Ana", "ana@correo.com");
        create("Luis", "luis@correo.com");

        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content(body("Otra", "ANA@correo.com", "secreto123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Ya existe un usuario con el correo ana@correo.com"));

        mvc.perform(put("/api/usuarios/" + id1).contentType(MediaType.APPLICATION_JSON)
                        .content(body("Ana", "luis@correo.com", null)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void missingUserReturns404AndPasswordRequiredOnCreate() throws Exception {
        mvc.perform(get("/api/usuarios/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Usuario no encontrado con id 999"));
        mvc.perform(put("/api/usuarios/999").contentType(MediaType.APPLICATION_JSON)
                        .content(body("X", "x@correo.com", "secreto123")))
                .andExpect(status().isNotFound());
        mvc.perform(delete("/api/usuarios/999")).andExpect(status().isNotFound());

        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content(body("Sin clave", "sinclave@correo.com", null)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La contraseña es obligatoria"));
        mvc.perform(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content(body("Mal", "no-es-correo", "secreto123")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.correo").exists());
    }
}
