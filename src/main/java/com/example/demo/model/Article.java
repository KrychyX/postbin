package com.example.demo.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс, представляющий статью в системе.
 * Статья связана с пользователем, который её создал.
 */
@Entity
@Table(name = "article")
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(exclude = "user")
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Заголовок статьи обязателен")
    @Size(min = 5, max = 100, message = "Заголовок должен быть от 5 до 100 символов")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Содержание статьи обязательно")
    @Size(min = 10, message = "Содержание должно быть не менее 10 символов")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Schema(hidden = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Конструктор по умолчанию, необходим для JPA.
     */
    public Article() {
        // Пустой конструктор требуется для JPA
    }
}