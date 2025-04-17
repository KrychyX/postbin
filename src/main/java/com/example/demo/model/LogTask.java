package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Модель задачи обработки логов.
 * Содержит информацию о статусе выполнения задачи и пути к результату.
 */
@Getter
@Setter
public class LogTask {
    private final String id;
    private final String date;
    private LogTaskStatus status;
    private String filePath;

    /**
     * Создает новую задачу обработки логов со всеми параметрами.
     *
     * @param id уникальный идентификатор задачи
     * @param date дата для фильтрации логов
     */
    public LogTask(String id, String date) {
        this.id = id;
        this.date = date;
        this.status = LogTaskStatus.PENDING;
    }
}