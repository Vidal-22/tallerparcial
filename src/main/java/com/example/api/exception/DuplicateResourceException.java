package com.example.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Se lanza cuando una operación violaría una regla de unicidad (por ejemplo, un correo ya registrado).
 * La guía de la actividad exige responder con HTTP 400.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class DuplicateResourceException extends RuntimeException {

    /**
     * @param message mensaje que se devuelve al cliente en el campo {@code error}
     */
    public DuplicateResourceException(String message) {
        super(message);
    }
}
