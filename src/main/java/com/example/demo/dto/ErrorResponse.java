package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.Data;

/**
 * DTO for representing error responses in the API.
 *
 * <p>Contains information about the error including HTTP status code, error message,
 * timestamp when the error occurred, and optional field validation errors.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> fieldErrors; // Для валидации полей

    /**
     * Constructs an ErrorResponse with status code and message.
     * Timestamp is automatically set to current time.
     *
     * @param status HTTP status code
     * @param message error description
     */
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}