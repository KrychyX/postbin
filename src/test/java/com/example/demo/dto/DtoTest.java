package com.example.demo.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DtoTest {

    @Test
    void articleDTO_GetterSetter_WorkCorrectly() {
        // Arrange
        ArticleDTO dto = new ArticleDTO();

        // Act
        dto.setId(1L);
        dto.setTitle("Test Title");
        dto.setContent("Test Content");
        dto.setAuthor("Test Author");

        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("Test Title", dto.getTitle());
        assertEquals("Test Content", dto.getContent());
        assertEquals("Test Author", dto.getAuthor());
    }

    @Test
    void userDTO_GetterSetter_WorkCorrectly() {
        // Arrange
        UserDTO dto = new UserDTO();

        // Act
        dto.setId(1L);
        dto.setName("Test User");
        dto.setEmail("test@example.com");

        ArticleDTO article1 = new ArticleDTO();
        article1.setId(1L);
        ArticleDTO article2 = new ArticleDTO();
        article2.setId(2L);
        dto.setArticles(List.of(article1, article2));

        dto.setSubscriptions(List.of("Sub1", "Sub2"));
        dto.setSubscribers(List.of("Subscriber1", "Subscriber2"));

        // Assert
        assertEquals(1L, dto.getId());
        assertEquals("Test User", dto.getName());
        assertEquals("test@example.com", dto.getEmail());
        assertEquals(2, dto.getArticles().size());
        assertEquals(2, dto.getSubscriptions().size());
        assertEquals(2, dto.getSubscribers().size());
    }

    @Test
    void errorResponse_ConstructorAndGetters_WorkCorrectly() {
        // Arrange
        int status = 400;
        String message = "Bad Request";
        Map<String, String> fieldErrors = new HashMap<>();
        fieldErrors.put("email", "Invalid email format");

        // Act
        ErrorResponse errorResponse = new ErrorResponse(status, message);
        errorResponse.setFieldErrors(fieldErrors);

        // Assert
        assertEquals(status, errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertNotNull(errorResponse.getTimestamp());
        assertEquals(1, errorResponse.getFieldErrors().size());
        assertEquals("Invalid email format", errorResponse.getFieldErrors().get("email"));
    }

    @Test
    void errorResponse_JsonInclude_WorksCorrectly() {
        // Arrange
        ErrorResponse errorResponse = new ErrorResponse(404, "Not Found");

        // Assert
        assertNull(errorResponse.getFieldErrors()); // Должно быть null, так как не установлено
    }
}