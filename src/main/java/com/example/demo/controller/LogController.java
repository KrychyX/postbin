package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling log file operations.
 *
 * <p>Provides endpoints for retrieving and downloading application log files filtered by date.
 */
@RestController
@RequestMapping("/api/logs")
@Tag(name = "Log Controller", description = "API для работы с логами")
public class LogController {

    private static final String LOG_FILE_PATH = "application.log";

    /**
     * Downloads log file filtered by specified date.
     *
     * <p>Reads the application log file, filters lines by the requested date, and returns them
     * as a downloadable file. If no logs exist for the specified date, returns 204 No Content.
     *
     * @param date the date to filter logs in format yyyy-MM-dd
     * @return ResponseEntity containing filtered log file or appropriate status code
     * @throws IOException if there's an error reading the log file
     */
    @Operation(summary = "Получить лог-файл за указанную дату",
            description = "Возвращает лог-файл, отфильтрованный по дате")
    @ApiResponse(responseCode = "200", description = "Лог-файл успешно получен")
    @GetMapping(value = "/download", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<FileSystemResource> getLogFileByDate(
            @Parameter(description = "Дата в формате yyyy-MM-dd")
            @RequestParam String date) throws IOException {

        LocalDate logDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        String dateString = logDate.format(DateTimeFormatter.ISO_DATE);

        Path logFilePath = Paths.get(LOG_FILE_PATH);
        if (!Files.exists(logFilePath)) {
            return ResponseEntity.notFound().build();
        }

        List<String> filteredLines = Files.lines(logFilePath)
                .filter(line -> line.contains(dateString))
                .collect(Collectors.toList());

        if (filteredLines.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        File tempFile = File.createTempFile("logs-" + dateString, ".log");
        Files.write(tempFile.toPath(), filteredLines);
        tempFile.deleteOnExit();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=logs-" + dateString + ".log")
                .body(new FileSystemResource(tempFile));
    }
}