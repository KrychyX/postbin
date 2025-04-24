package com.example.demo.controller;

import com.example.demo.service.VisitCounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller that provides statistics related to user visits.
 */
@RestController
@RequestMapping("/stats")
public class StatsController {
    private final VisitCounterService visitCounterService;

    /**
     * Constructs the controller with the given {@link VisitCounterService}.
     */
    public StatsController(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    /**
     * Returns the number of user visits.
     */
    @GetMapping("/users/visits")
    public ResponseEntity<Integer> getUserVisitsCount() {
        return ResponseEntity.ok(visitCounterService.getCount());
    }
}