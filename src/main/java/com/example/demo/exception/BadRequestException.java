package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a client sends a malformed or invalid request.
 *
 * <p>This exception is annotated with {@code @ResponseStatus(HttpStatus.BAD_REQUEST)} which results
 * in an HTTP 400 (Bad Request) status code being returned to the client when this exception is
 * thrown from a controller method.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    /**
     * Constructs a new BadRequestException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public BadRequestException(String message) {
        super(message);
    }
}