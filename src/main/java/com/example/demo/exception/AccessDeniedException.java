package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when access to a resource is denied due to insufficient permissions.
 * This exception is annotated with {@code @ResponseStatus(HttpStatus.FORBIDDEN)} which
 * results in an HTTP 403 (Forbidden) status code being returned to the client when this
 * exception is thrown from a controller method.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {

    /**
     * Constructs a new AccessDeniedException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public AccessDeniedException(String message) {
        super(message);
    }
}