package com.example.demo.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

/**
 * Service for counting requests to various endpoints.
 */
@Service
public class CounterService {
    private final ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

    /**
     * Increments the request count for the specified endpoint.
     */
    public void increment(String endpoint) {
        counters.computeIfAbsent(endpoint, k -> new AtomicLong(0))
                .incrementAndGet();
    }

    /**
     * Returns the current request count for the specified endpoint.
     */
    public long getCount(String endpoint) {
        return counters.getOrDefault(endpoint, new AtomicLong(0)).get();
    }

    /**
     * Returns the request counts for all tracked endpoints.
     */
    public ConcurrentMap<String, Long> getAllStats() {
        ConcurrentMap<String, Long> result = new ConcurrentHashMap<>();
        counters.forEach((key, value) -> result.put(key, value.get()));
        return result;
    }
}