package com.example.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Se lanza cuando se busca un recurso (producto o usuario) que no existe. Se traduce en HTTP 404.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * @param message mensaje que se devuelve al cliente en el campo {@code error}
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
