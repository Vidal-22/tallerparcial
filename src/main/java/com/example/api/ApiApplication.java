package com.example.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la API REST de la tienda (módulos de Productos y Usuarios).
 */
@SpringBootApplication
public class ApiApplication {

	/**
	 * Arranca la aplicación Spring Boot.
	 *
	 * @param args argumentos de línea de comandos
	 */
	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}
