package com.example.demo.service;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Article;
import com.example.demo.model.User;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.CacheUtil;
import jakarta.transaction.Transactional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы со статьями.
 * Предоставляет методы для создания, получения, обновления и удаления статей.
 */
@Service
public class ArticleService {

    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final CacheUtil<String, List<Article>> articleCacheByAuthor;

    /**
     * Конструктор для внедрения зависимостей {@link ArticleRepository} и {@link UserRepository}.
     *
     * @param articleRepository репозиторий для работы со статьями
     * @param userRepository    репозиторий для работы с пользователями
     */
    public ArticleService(ArticleRepository articleRepository,
                          UserRepository userRepository,
                          CacheUtil<String, List<Article>> articleCacheByAuthor) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.articleCacheByAuthor = articleCacheByAuthor;
    }

    /**
     * Создает новую статью для указанного пользователя.
     *
     * @param userId  идентификатор пользователя
     * @param article данные статьи
     * @return сохраненная статья
     */
    @Transactional
    public Article createArticle(Long userId, Article article) {
        logger.info("Попытка создания статьи для пользователя с ID: {}", userId);
        if (userId == null) {
            logger.error("ID пользователя не может быть null");
            throw new BadRequestException("User ID cannot be null");
        }
        if (article == null || article.getTitle() == null || article.getTitle().isBlank()) {
            logger.error("Заголовок статьи обязателен");
            throw new BadRequestException("Article title is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", userId);
                    return new ResourceNotFoundException("Пользователь с ID "
                            + userId + " не найден");
                });

        article.setUser(user);
        Article savedArticle = articleRepository.save(article);
        logger.info("Статья успешно создана с ID: {}", savedArticle.getId());

        return savedArticle;
    }

    /**
     * Возвращает список всех статей.
     *
     * @return список всех статей
     */
    public List<Article> getAllArticles() {
        logger.info("Получение списка всех статей");
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
        logger.info("Получение статьи по ID: {}", id);
        if (id == null) {
            logger.error("ID статьи не может быть null");
            throw new BadRequestException("ID статьи не может быть null");
        }
        return articleRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Статья с ID {} не найдена", id);
                    return new ResourceNotFoundException("Статья с ID " + id + " не найдена");
                });
    }

    /**
     * Возвращает список статей, созданных указанным пользователем.
     *
     * @param userId идентификатор пользователя
     * @return список статей пользователя
     */
    public List<Article> getArticlesByUserId(Long userId) {
        logger.info("Получение статей пользователя с ID: {}", userId);
        return articleRepository.findByUserId(userId);
    }

    /**
     * Обновляет статью по её идентификатору.
     *
     * @param id            идентификатор статьи
     * @param articleDetails новые данные статьи
     * @return обновленная статья
     */
    @Transactional
    public Article updateArticle(Long id, Article articleDetails) {
        logger.info("Обновление статьи с ID: {}", id);
        Article article = getArticleById(id);

        if (articleDetails.getTitle() != null) {
            logger.debug("Обновление заголовка для статьи с ID: {}", id);
            article.setTitle(articleDetails.getTitle());
        }
        if (articleDetails.getContent() != null) {
            logger.debug("Обновление содержимого для статьи с ID: {}", id);
            article.setContent(articleDetails.getContent());
        }

        Article updatedArticle = articleRepository.save(article);
        logger.info("Статья с ID {} успешно обновлена", id);
        return updatedArticle;
    }

    /**
     * Удаляет статью по её идентификатору.
     *
     * @param id идентификатор статьи
     */
    @Transactional
    public void deleteArticle(Long id) {
        logger.info("Удаление статьи с ID: {}", id);
        Article article = getArticleById(id);
        articleRepository.delete(article);
        logger.info("Статья с ID {} успешно удалена", id);
    }

    /**
     * Ищет статьи по имени автора. Сначала проверяет, есть ли данные в кэше.
     * Если данные найдены, они возвращаются из кэша.
     * В противном случае выполняется запрос к базе данных, а затем результат кэшируется.
     *
     * @param authorName имя автора, по которому выполняется поиск статей
     * @return список статей, написанных указанным автором
     */
    public List<Article> findByAuthorName(String authorName) {
        logger.info("Поиск статей автора:");
        if (authorName == null || authorName.isBlank()) {
            logger.error("Имя автора не может быть пустым");
            throw new BadRequestException("Имя автора не может быть пустым");
        }

        List<Article> articles = articleCacheByAuthor.get(authorName);
        if (articles == null) {
            logger.debug("Данные для автора не найдены в кэше, запрос к БД");
            articles = articleRepository.findByAuthorName(authorName);
            if (articles.isEmpty()) {
                logger.error("Статьи автора не найдены");
                throw new ResourceNotFoundException("Статьи автора " + authorName + " не найдены");
            }
            articleCacheByAuthor.put(authorName, articles);
            logger.debug("Данные автора сохранены в кэш");
        } else {
            logger.debug("Данные автора найдены в кэше");
        }
        return articles;
    }

}