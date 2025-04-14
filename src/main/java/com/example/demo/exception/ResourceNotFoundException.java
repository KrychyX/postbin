package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found.
 *
 * <p>This exception results in an HTTP 404 (Not Found) status code being returned
 * to the client when thrown from a controller method.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message describing which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}