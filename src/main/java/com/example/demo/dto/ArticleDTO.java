package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс DTO (Data Transfer Object) для передачи данных о статье.
 * Используется для представления информации о статье в API.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Getter
@Setter
public class ArticleDTO {

    private Long id;
    private String title;
    private String content;
    private String author;
}