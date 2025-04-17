package com.example.demo.controller;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.mapper.ArticleMapper;
import com.example.demo.model.Article;
import com.example.demo.service.ArticleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления статьями.
 * Предоставляет REST API для создания, получения, обновления и удаления статей.
 */
@RestController
@RequestMapping("/articles")
@Tag(name = "Article Controller", description = "API для управления статьями")
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
    @Operation(summary = "Создать новую статью",
            description = "Создает новую статью для указанного пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статья успешно создана"),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping("/user/{userId}")
    public ArticleDTO createArticle(
            @Parameter(description = "Данные статьи") @RequestBody @Valid Article article,
            @Parameter(description = "ID пользователя") @PathVariable Long userId
    ) {
        Article createdArticle = articleService.createArticle(userId, article);
        return ArticleMapper.toDTO(createdArticle);
    }

    /**
     * Возвращает список всех статей.
     *
     * @return список DTO статей
     */
    @Operation(summary = "Получить все статьи", description = "Возвращает список всех статей")
    @ApiResponse(responseCode = "200", description = "Список статей успешно получен")
    @GetMapping
    public List<ArticleDTO> getAllArticles() {
        return articleService.getAllArticles()
                .stream()
                .map(ArticleMapper::toDTO)
                .toList();
    }

    /**
     * Возвращает статью по её идентификатору.
     *
     * @param id идентификатор статьи
     * @return DTO статьи
     */
    @Operation(summary = "Получить статью по ID",
            description = "Возвращает статью по её идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статья успешно найдена"),
        @ApiResponse(responseCode = "404", description = "Статья не найдена")
    })
    @GetMapping("/{id}")
    public ArticleDTO getArticleById(
            @Parameter(description = "ID статьи") @PathVariable Long id
    ) {
        Article article = articleService.getArticleById(id);
        return ArticleMapper.toDTO(article);
    }

    /**
     * Возвращает список статей, созданных указанным пользователем.
     *
     * @param userId идентификатор пользователя
     * @return список DTO статей
     */
    @Operation(summary = "Получить статьи пользователя",
            description = "Возвращает список статей, созданных указанным пользователем")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список статей успешно получен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/user/{userId}")
    public List<ArticleDTO> getArticlesByUserId(
            @Parameter(description = "ID пользователя") @PathVariable Long userId
    ) {
        return articleService.getArticlesByUserId(userId)
                .stream()
                .map(ArticleMapper::toDTO)
                .toList();
    }

    /**
     * Обновляет статью по её идентификатору.
     *
     * @param id            идентификатор статьи
     * @param articleDetails новые данные статьи
     * @return DTO обновленной статьи
     */
    @Operation(summary = "Обновить статью", description = "Обновляет статью по её идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статья успешно обновлена"),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
        @ApiResponse(responseCode = "404", description = "Статья не найдена")
    })
    @PutMapping("/{id}")
    public ArticleDTO updateArticle(
            @Parameter(description = "ID статьи") @PathVariable Long id,
            @Parameter(description = "Новые данные статьи") @RequestBody Article articleDetails
    ) {
        Article updatedArticle = articleService.updateArticle(id, articleDetails);
        return ArticleMapper.toDTO(updatedArticle);
    }

    /**
     * Удаляет статью по её идентификатору.
     *
     * @param id идентификатор статьи
     */
    @Operation(summary = "Удалить статью", description = "Удаляет статью по её идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статья успешно удалена"),
        @ApiResponse(responseCode = "404", description = "Статья не найдена")
    })
    @DeleteMapping("/{id}")
    public void deleteArticle(
            @Parameter(description = "ID статьи") @PathVariable Long id
    ) {
        articleService.deleteArticle(id);
    }

    /**
     * Возвращает список статей по имени автора.
     *
     * @param authorName имя автора
     * @return список DTO статей
     */
    @Operation(summary = "Поиск статей по имени автора",


            description = "Возвращает список статей по имени автора")
    @ApiResponse(responseCode = "200", description = "Список статей успешно получен")
    @GetMapping("/author")
    public List<ArticleDTO> getArticlesByAuthorName(
            @Parameter(description = "Имя автора") @RequestParam String authorName
    ) {
        return articleService.findByAuthorName(authorName)
                .stream()
                .map(ArticleMapper::toDTO)
                .toList();
    }

    /**
     * Создает несколько статей для указанного пользователя.
     *
     * @param articles список статей
     * @param userId  идентификатор пользователя
     * @return список DTO созданных статей
     */
    @Operation(summary = "Создать несколько статей",
            description = "Создает несколько статей для указанного пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Статьи успешно созданы"),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PostMapping("/bulk/user/{userId}")
    public List<ArticleDTO> createArticlesBulk(
            @Parameter(description = "Список статей") @RequestBody @Valid List<Article> articles,
            @Parameter(description = "ID пользователя") @PathVariable Long userId
    ) {
        return articles.stream()
                .map(article -> articleService.createArticle(userId, article))
                .map(ArticleMapper::toDTO)
                .toList();
    }
}