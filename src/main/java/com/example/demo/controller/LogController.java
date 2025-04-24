package com.example.demo.controller;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.LogTask;
import com.example.demo.model.LogTaskStatus;
import com.example.demo.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
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
    @ApiResponse(responseCode = "400", description = "Invalid date format")
    @PostMapping
    public ResponseEntity<String> createLogTask(
            @RequestParam String date) {
        String taskId = logService.createLogTask(date);
        return ResponseEntity.ok(taskId);
    }

    /**
     * Возвращает статус задачи обработки логов.
     *
     * @param taskId ID задачи для проверки
     * @return ResponseEntity с объектом LogTask, содержащим статус
     */
    @Operation(summary = "Get task status")
    @GetMapping("/{taskId}/status")
    public ResponseEntity<Map<String, Object>> getTaskStatus(
            @PathVariable String taskId) {
        LogTask task = logService.getTaskStatus(taskId);
        return ResponseEntity.ok(Map.of(
                "status", task.getStatus().name()
        ));
    }

    /**
     * Скачивает обработанный файл логов.
     *
     * @param taskId ID задачи для скачивания
     * @return ResponseEntity с файлом логов в виде Resource
     */
    @Operation(summary = "Download log file")
    @ApiResponse(responseCode = "200", description = "File downloaded")
    @ApiResponse(responseCode = "404", description = "Task or file not found")
    @ApiResponse(responseCode = "425", description = "File not ready yet")
    @GetMapping("/{taskId}/download")
    public ResponseEntity<Object> downloadLogFile(@PathVariable String taskId) {
        try {
            LogTask task = logService.getTaskStatus(taskId);

            if (task.getStatus() != LogTaskStatus.COMPLETED) {
                return ResponseEntity.ok()
                        .body(Map.of(
                                "status", task.getStatus().name(),
                                "message", "Еще не готов"
                        ));
            }

            Path filePath = Paths.get(task.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"logs-" + task.getDate() + ".log\"")
                    .body(resource);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Internal server error"));
        }
    }
}