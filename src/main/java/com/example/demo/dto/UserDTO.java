package com.example.demo.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс DTO (Data Transfer Object) для передачи данных о пользователе.
 * Используется для представления информации о пользователе в API.
 */
@SuppressWarnings("checkstyle:AbbreviationAsWordInName")
@Getter
@Setter
public class UserDTO {

    private Long id;
    private String name;
    private String email;
    private List<ArticleDTO> articles;
    private List<String> subscriptions;
    private List<String> subscribers;
}