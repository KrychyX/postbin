package com.example.demo.controller;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.mapper.ArticleMapper;
import com.example.demo.model.Article;
import com.example.demo.service.ArticleService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления статьями.
 * Предоставляет REST API для создания, получения, обновления и удаления статей.
 */
@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    /**
     * Конструктор для внедрения зависимости ArticleService.
     *
     * @param articleService сервис для работы со статьями
     */
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * Создает новую статью для указанного пользователя.
     *
     * @param article данные статьи
     * @param userId  идентификатор пользователя
     * @return DTO созданной статьи
     */
    @PostMapping("/user/{userId}")
    public ArticleDTO createArticle(
            @RequestBody Article article,
            @PathVariable Long userId
    ) {
        Article createdArticle = articleService.createArticle(userId, article);
        return ArticleMapper.toDTO(createdArticle);
    }

    /**
     * Возвращает список всех статей.
     *
     * @return список DTO статей
     */
    @GetMapping
    public List<ArticleDTO> getAllArticles() {
        return articleService.getAllArticles()
                .stream()
                .map(ArticleMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает статью по её идентификатору.
     *
     * @param id идентификатор статьи
     * @return DTO статьи
     */
    @GetMapping("/{id}")
    public ArticleDTO getArticleById(@PathVariable Long id) {
        Article article = articleService.getArticleById(id);
        return ArticleMapper.toDTO(article);
    }

    /**
     * Возвращает список статей, созданных указанным пользователем.
     *
     * @param userId идентификатор пользователя
     * @return список DTO статей
     */
    @GetMapping("/user/{userId}")
    public List<ArticleDTO> getArticlesByUserId(@PathVariable Long userId) {
        return articleService.getArticlesByUserId(userId)
                .stream()
                .map(ArticleMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Обновляет статью по её идентификатору.
     *
     * @param id            идентификатор статьи
     * @param articleDetails новые данные статьи
     * @return DTO обновленной статьи
     */
    @PutMapping("/{id}")
    public ArticleDTO updateArticle(
            @PathVariable Long id,
            @RequestBody Article articleDetails
    ) {
        Article updatedArticle = articleService.updateArticle(id, articleDetails);
        return ArticleMapper.toDTO(updatedArticle);
    }

    /**
     * Удаляет статью по её идентификатору.
     *
     * @param id идентификатор статьи
     */
    @DeleteMapping("/{id}")
    public void deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
    }
}