package com.example.demo.repository;

import com.example.demo.model.LogTask;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для хранения и управления задачами обработки логов.
 * Использует thread-safe реализацию для хранения задач в памяти.
 */
@Repository
public class LogTaskRepository {

    private final Map<String, LogTask> storage = new ConcurrentHashMap<>();

    /**
     * Сохраняет или обновляет задачу в хранилище.
     *
     * @param task задача для сохранения
     * @return сохраненная задача
     * @throws IllegalArgumentException если задача или её ID равен null
     */
    public LogTask save(LogTask task) {
        storage.put(task.getId(), task);
        return task;
    }

    /**
     * Находит задачу по её идентификатору.
     *
     * @param taskId идентификатор задачи
     * @return Optional с найденной задачей или пустой Optional, если задача не найдена
     * @throws IllegalArgumentException если taskId равен null
     */
    public Optional<LogTask> findById(String taskId) {
        return Optional.ofNullable(storage.get(taskId));
    }
}