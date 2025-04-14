package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    @InjectMocks
    private LogController logController;

    @Test
    void getLogFileByDate_NoMatchingEntries_ReturnsNoContent() {
        // Arrange
        String testDate = LocalDate.now().plusDays(1).toString(); // Future date with no logs

        // Act
        ResponseEntity<FileSystemResource> response = logController.getLogFileByDate(testDate);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}