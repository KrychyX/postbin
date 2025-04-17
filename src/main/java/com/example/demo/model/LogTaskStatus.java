package com.example.demo.model;

/**
 * Перечисление статусов задачи обработки логов.
 * Определяет возможные состояния задачи в процессе её выполнения.
 */
public enum LogTaskStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}
