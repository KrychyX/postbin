package com.example.demo.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTest {

    @Test
    void accessDeniedException_HasCorrectResponseStatus() throws NoSuchFieldException {
        ResponseStatus annotation = AccessDeniedException.class.getAnnotation(ResponseStatus.class);
        assertNotNull(annotation);
        assertEquals(HttpStatus.FORBIDDEN, annotation.value());
    }

    @Test
    void accessDeniedException_ConstructorSetsMessage() {
        String message = "Access denied";
        AccessDeniedException exception = new AccessDeniedException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void badRequestException_HasCorrectResponseStatus() {
        ResponseStatus annotation = BadRequestException.class.getAnnotation(ResponseStatus.class);
        assertNotNull(annotation);
        assertEquals(HttpStatus.BAD_REQUEST, annotation.value());
    }

    @Test
    void badRequestException_ConstructorSetsMessage() {
        String message = "Bad request";
        BadRequestException exception = new BadRequestException(message);
        assertEquals(message, exception.getMessage());
    }

    @Test
    void resourceNotFoundException_HasCorrectResponseStatus() {
        ResponseStatus annotation = ResourceNotFoundException.class.getAnnotation(ResponseStatus.class);
        assertNotNull(annotation);
        assertEquals(HttpStatus.NOT_FOUND, annotation.value());
    }

    @Test
    void resourceNotFoundException_ConstructorSetsMessage() {
        String message = "Resource not found";
        ResourceNotFoundException exception = new ResourceNotFoundException(message);
        assertEquals(message, exception.getMessage());
    }
}