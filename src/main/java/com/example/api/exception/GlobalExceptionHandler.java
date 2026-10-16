package com.example.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejador global que convierte las excepciones de ambos módulos en respuestas JSON con el formato
 * {@code {"error": "mensaje"}}, sin exponer trazas ni detalles internos.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Recurso inexistente.
     *
     * @param ex excepción lanzada por el servicio
     * @return 404 con el mensaje de error
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Violación de una regla de unicidad o de negocio.
     *
     * @param ex excepción lanzada por el servicio
     * @return 400 con el mensaje de error
     */
    @ExceptionHandler({DuplicateResourceException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(RuntimeException ex) {
        return error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Errores de validación de los campos del cuerpo ({@code @Valid}).
     *
     * @param ex excepción con los campos inválidos
     * @return 400 con el mensaje general y el detalle por campo
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fields.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", "Datos inválidos");
        body.put("campos", fields);
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Cuerpo JSON mal formado o con tipos incorrectos.
     *
     * @param ex excepción de lectura del cuerpo
     * @return 400 con un mensaje genérico
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex) {
        return error(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es un JSON válido");
    }

    /**
     * Parámetro de ruta con un tipo incorrecto, por ejemplo {@code /api/productos/abc}.
     *
     * @param ex excepción de conversión
     * @return 400 indicando el parámetro inválido
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return error(HttpStatus.BAD_REQUEST, "Valor inválido para el parámetro '" + ex.getName() + "'");
    }

    /**
     * Restricción de la base de datos violada (por ejemplo, dos altas simultáneas con el mismo correo).
     *
     * @param ex excepción de integridad
     * @return 400 con un mensaje genérico, sin detalles de la base de datos
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleIntegrity(DataIntegrityViolationException ex) {
        log.warn("Violación de integridad de datos: {}", ex.getMostSpecificCause().getMessage());
        return error(HttpStatus.BAD_REQUEST, "La operación viola una restricción de datos (posible valor duplicado)");
    }

    /**
     * Cualquier otra excepción. Las excepciones propias de Spring MVC (ruta inexistente, método no
     * permitido, etc.) conservan su código; el resto se registra y se responde con 500.
     *
     * @param ex excepción no controlada
     * @return la respuesta con el código correspondiente y un mensaje sin detalles internos
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOther(Exception ex) {
        if (ex instanceof ErrorResponse errorResponse) {
            HttpStatusCode status = errorResponse.getStatusCode();
            String detail = errorResponse.getBody().getDetail();
            return error(status, detail != null ? detail : "Error en la petición");
        }
        log.error("Error inesperado", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }

    private ResponseEntity<Map<String, Object>> error(HttpStatusCode status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", message);
        return ResponseEntity.status(status).body(body);
    }
}
