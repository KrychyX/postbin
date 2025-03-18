package com.example.demo.service;

import com.example.demo.model.Article;
import com.example.demo.model.User;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы со статьями.
 * Предоставляет методы для создания, получения, обновления и удаления статей.
 */
@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;

    /**
     * Конструктор для внедрения зависимостей {@link ArticleRepository} и {@link UserRepository}.
     *
     * @param articleRepository репозиторий для работы со статьями
     * @param userRepository    репозиторий для работы с пользователями
     */
    public ArticleService(ArticleRepository articleRepository, UserRepository userRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
    }

    /**
     * Создает новую статью для указанного пользователя.
     *
     * @param userId  идентификатор пользователя
     * @param article данные статьи
     * @return сохраненная статья
     */
    public Article createArticle(Long userId, Article article) {
        User user = userRepository.findById(userId).orElse(null);
        article.setUser(user);
        return articleRepository.save(article);
    }

    /**
     * Возвращает список всех статей.
     *
     * @return список всех статей
     */
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    /**
     * Возвращает статью по её идентификатору.
     *
     * @param id идентификатор статьи
     * @return найденная статья
     * @throws RuntimeException если статья не найдена
     */
    public Article getArticleById(Long id) {
        return articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id: " + id));
    }

    /**
     * Возвращает список статей, созданных указанным пользователем.
     *
     * @param userId идентификатор пользователя
     * @return список статей пользователя
     */
    public List<Article> getArticlesByUserId(Long userId) {
        return articleRepository.findByUserId(userId);
    }

    /**
     * Обновляет статью по её идентификатору.
     *
     * @param id            идентификатор статьи
     * @param articleDetails новые данные статьи
     * @return обновленная статья
     */
    public Article updateArticle(Long id, Article articleDetails) {
        Article article = getArticleById(id);
        article.setTitle(articleDetails.getTitle());
        article.setContent(articleDetails.getContent());
        return articleRepository.save(article);
    }

    /**
     * Удаляет статью по её идентификатору.
     *
     * @param id идентификатор статьи
     */
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }
}