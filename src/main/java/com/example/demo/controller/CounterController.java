package com.example.demo.controller;

import com.example.demo.service.CounterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Class to control visits. */
@RestController
@RequestMapping("/stats")
@Tag(name = "Statistics", description = "API для получения статистики")
public class CounterController {
    private final CounterService counterService;

    /**
     * Increments the request count for the specified endpoint.
     */
    public CounterController(CounterService counterService) {
        this.counterService = counterService;
    }

    @Operation(summary = "Получить всю статистику")
    @GetMapping("/articles/visits")
    public ResponseEntity<Map<String, Long>> getAllStats() {
        return ResponseEntity.ok(counterService.getAllStats());
    }
}
