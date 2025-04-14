package com.example.demo.exception;

import com.example.demo.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    void handleValidationExceptions_ReturnsCorrectResponse() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("object", "field", "default message");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Collections.singletonList(fieldError));

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertEquals(1, response.getBody().getFieldErrors().size());
        assertEquals("default message", response.getBody().getFieldErrors().get("field"));
    }

    @Test
    void handleBadRequest_ReturnsCorrectResponse() {
        // Arrange
        String message = "Bad request message";
        BadRequestException ex = new BadRequestException(message);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBadRequest(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(message, response.getBody().getMessage());
        assertNull(response.getBody().getFieldErrors());
    }

    @Test
    void handleResourceNotFound_ReturnsCorrectResponse() {
        // Arrange
        String message = "Resource not found";
        ResourceNotFoundException ex = new ResourceNotFoundException(message);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFound(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(message, response.getBody().getMessage());
    }

    @Test
    void handleAllExceptions_ReturnsCorrectResponse() {
        // Arrange
        String message = "Internal error";
        Exception ex = new Exception(message);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleAllExceptions(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains(message));
    }
}