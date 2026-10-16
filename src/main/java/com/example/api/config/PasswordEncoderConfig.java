package com.example.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración del codificador de contraseñas. Solo usa el módulo de criptografía de Spring Security,
 * sin activar autenticación ni filtros de seguridad.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Codificador BCrypt para guardar las contraseñas con hash.
     *
     * @return el codificador de contraseñas
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
