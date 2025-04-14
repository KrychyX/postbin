package com.example.demo.controller;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.model.Article;
import com.example.demo.model.User;
import com.example.demo.service.ArticleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleControllerTest {

    @Mock
    private ArticleService articleService;

    @InjectMocks
    private ArticleController articleController;

    private User createTestUser() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        return user;
    }

    private Article createTestArticle() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Title");
        article.setContent("Test Content");
        article.setUser(createTestUser()); // Устанавливаем пользователя
        return article;
    }

    @Test
    void createArticle_ValidData_ReturnsArticleDTO() {
        // Arrange
        Article article = createTestArticle();
        Article savedArticle = createTestArticle();
        when(articleService.createArticle(anyLong(), any(Article.class))).thenReturn(savedArticle);

        // Act
        ArticleDTO result = articleController.createArticle(article, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(articleService, times(1)).createArticle(1L, article);
    }

    @Test
    void getAllArticles_ReturnsListOfArticleDTOs() {
        // Arrange
        Article article1 = createTestArticle();
        Article article2 = createTestArticle();
        article2.setId(2L);
        when(articleService.getAllArticles()).thenReturn(Arrays.asList(article1, article2));

        // Act
        List<ArticleDTO> result = articleController.getAllArticles();

        // Assert
        assertEquals(2, result.size());
        verify(articleService, times(1)).getAllArticles();
    }

    @Test
    void getArticleById_ValidId_ReturnsArticleDTO() {
        // Arrange
        Article article = createTestArticle();
        when(articleService.getArticleById(1L)).thenReturn(article);

        // Act
        ArticleDTO result = articleController.getArticleById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(articleService, times(1)).getArticleById(1L);
    }

    @Test
    void getArticlesByUserId_ReturnsListOfArticleDTOs() {
        // Arrange
        Article article1 = createTestArticle();
        Article article2 = createTestArticle();
        article2.setId(2L);
        when(articleService.getArticlesByUserId(1L)).thenReturn(Arrays.asList(article1, article2));

        // Act
        List<ArticleDTO> result = articleController.getArticlesByUserId(1L);

        // Assert
        assertEquals(2, result.size());
        verify(articleService, times(1)).getArticlesByUserId(1L);
    }

    @Test
    void updateArticle_ValidData_ReturnsUpdatedArticleDTO() {
        // Arrange
        Article articleDetails = createTestArticle();
        articleDetails.setTitle("Updated Title");
        Article updatedArticle = createTestArticle();
        when(articleService.updateArticle(anyLong(), any(Article.class))).thenReturn(updatedArticle);

        // Act
        ArticleDTO result = articleController.updateArticle(1L, articleDetails);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(articleService, times(1)).updateArticle(1L, articleDetails);
    }

    @Test
    void deleteArticle_ValidId_CallsService() {
        // Act
        articleController.deleteArticle(1L);

        // Assert
        verify(articleService, times(1)).deleteArticle(1L);
    }

    @Test
    void getArticlesByAuthorName_ReturnsListOfArticleDTOs() {
        // Arrange
        Article article1 = createTestArticle();
        Article article2 = createTestArticle();
        article2.setId(2L);
        when(articleService.findByAuthorName("Author")).thenReturn(Arrays.asList(article1, article2));

        // Act
        List<ArticleDTO> result = articleController.getArticlesByAuthorName("Author");

        // Assert
        assertEquals(2, result.size());
        verify(articleService, times(1)).findByAuthorName("Author");
    }

    @Test
    void createArticlesBulk_ValidData_ReturnsListOfArticleDTOs() {
        // Arrange
        Article article1 = createTestArticle();
        Article article2 = createTestArticle();
        article2.setId(2L);
        List<Article> articles = Arrays.asList(article1, article2);

        Article savedArticle1 = createTestArticle();
        Article savedArticle2 = createTestArticle();
        savedArticle2.setId(2L);
        when(articleService.createArticle(anyLong(), eq(article1))).thenReturn(savedArticle1);
        when(articleService.createArticle(anyLong(), eq(article2))).thenReturn(savedArticle2);

        // Act
        List<ArticleDTO> result = articleController.createArticlesBulk(articles, 1L);

        // Assert
        assertEquals(2, result.size());
        verify(articleService, times(2)).createArticle(anyLong(), any(Article.class));
    }
}