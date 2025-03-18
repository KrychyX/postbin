package com.example.demo.mapper;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.model.Article;

/**
 * Класс-mapper для преобразования сущности {@link Article} в DTO {@link ArticleDTO}.
 * Предоставляет статические методы для конвертации данных.
 */
public class ArticleMapper {

    /**
     * Преобразует сущность {@link Article} в {@link ArticleDTO}.
     *
     * @param article сущность статьи, которую необходимо преобразовать
     * @return объект {@link ArticleDTO}, содержащий данные статьи
     */
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public static ArticleDTO toDTO(Article article) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setContent(article.getContent());
        dto.setAuthor(article.getUser().getName()); // Имя автора
        return dto;
    }
}