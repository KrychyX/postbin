package com.example.demo.service;

import org.springframework.stereotype.Service;

/**
 * Service for counting user visits to a specific endpoint.
 * Uses synchronization to ensure thread safety.
 */
@Service
public class VisitCounterService {
    private int count = 0;

    /**
     * Increments the visit count in a thread-safe manner.
     */
    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}