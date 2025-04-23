package com.example.demo.controller;

import com.example.demo.model.LogTask;
import com.example.demo.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * Контроллер для обработки операций с логами.
 */
@Tag(name = "Log API")
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    /**
     * Запускает асинхронную обработку файла логов.
     *
     * @param date Дата в формате yyyy-MM-dd
     * @return CompletableFuture с ResponseEntity, содержащим ID задачи
     */
    @Operation(summary = "Create log task", description = "Starts async log file processing")
    @ApiResponse(responseCode = "202", description = "Task accepted")
    @PostMapping
    public ResponseEntity<String> createLogTask(
            @RequestParam String date) {
        String taskId = logService.createLogTask(date);
        return ResponseEntity.accepted().body(taskId);
    }

    /**
     * Возвращает статус задачи обработки логов.
     *
     * @param taskId ID задачи для проверки
     * @return ResponseEntity с объектом LogTask, содержащим статус
     */
    @Operation(summary = "Get task status")
    @GetMapping("/{taskId}/status")
    public ResponseEntity<LogTask> getTaskStatus(
            @PathVariable String taskId) {
        return ResponseEntity.ok(logService.getTaskStatus(taskId));
    }

    /**
     * Скачивает обработанный файл логов.
     *
     * @param taskId ID задачи для скачивания
     * @return ResponseEntity с файлом логов в виде Resource
     */
    @Operation(summary = "Download log file")
    @GetMapping("/{taskId}/download")
    public ResponseEntity<Resource> downloadLogFile(
            @Parameter(description = "Task ID")
            @PathVariable String taskId) {
        try {
            Path filePath = logService.getLogFilePath(taskId);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "File not found or not readable");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filePath.getFileName() + "\"")
                    .body(resource);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не найдено", e);
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Не готов еще", e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to download file", e);
        }
    }
}