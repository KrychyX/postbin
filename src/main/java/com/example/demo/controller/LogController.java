package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(LogController.class);
    private static final String LOG_FILE_PATH = "application.log";
    private static final Set<PosixFilePermission> TEMP_FILE_PERMISSIONS =
            EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE);
    private static final String LOG_FILE_PREFIX = "logs-";
    private static final String LOG_FILE_SUFFIX = ".log";

    /**
     * Downloads log file filtered by specified date.
     *
     * <p>Reads the application log file, filters lines by the requested date, and returns them
     * as a downloadable file. If no logs exist for the specified date, returns 204 No Content.
     *
     * @param date the date to filter logs in format yyyy-MM-dd
     * @return ResponseEntity containing filtered log file or appropriate status code
     */
    @Operation(summary = "Получить лог-файл за указанную дату",
            description = "Возвращает лог-файл, отфильтрованный по дате")
    @ApiResponse(responseCode = "200", description = "Лог-файл успешно получен")
    @GetMapping(value = "/download", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<FileSystemResource> getLogFileByDate(
            @Parameter(description = "Дата в формате yyyy-MM-dd")
            @RequestParam String date) {

        if (date == null || date.isEmpty()) {
            logger.warn("Empty date parameter received");
            return ResponseEntity.badRequest().build();
        }

        try {
            LocalDate logDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
            String dateString = logDate.format(DateTimeFormatter.ISO_DATE);

            Path logFilePath = Paths.get(LOG_FILE_PATH).normalize().toAbsolutePath();
            if (!Files.exists(logFilePath)) {
                logger.warn("Log file not found at path: {}", logFilePath);
                return ResponseEntity.notFound().build();
            }

            List<String> filteredLines = Files.lines(logFilePath)
                    .filter(line -> line.contains(dateString))
                    .collect(Collectors.toList());

            if (filteredLines.isEmpty()) {
                logger.info("No logs found for date: {}", dateString);
                return ResponseEntity.noContent().build();
            }

            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"));
            Path tempFile = Files.createTempFile(tempDir,
                    LOG_FILE_PREFIX + dateString + "_",
                    LOG_FILE_SUFFIX);

            try {
                Files.write(tempFile, filteredLines,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);

                try {
                    Files.setPosixFilePermissions(tempFile, TEMP_FILE_PERMISSIONS);
                } catch (UnsupportedOperationException e) {
                    logger.debug("POSIX file permissions not supported on this system");
                }

                tempFile.toFile().deleteOnExit();

                String contentDisposition = "attachment; filename="
                        + LOG_FILE_PREFIX + dateString + LOG_FILE_SUFFIX;

                return ResponseEntity.ok()
                        .header("Content-Disposition", contentDisposition)
                        .body(new FileSystemResource(tempFile));
            } catch (IOException e) {
                logger.error("Failed to write to temporary file", e);
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ex) {
                    logger.error("Failed to delete temporary file", ex);
                }
                return ResponseEntity.internalServerError().build();
            }
        } catch (Exception e) {
            logger.error("Error processing log file request for date: {}", date, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}