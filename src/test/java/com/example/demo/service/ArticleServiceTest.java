package com.example.demo.service;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Article;
import com.example.demo.model.User;
import com.example.demo.repository.ArticleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.utils.CacheUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CacheUtil<String, List<Article>> articleCacheByAuthor;

    @InjectMocks
    private ArticleService articleService;

    private User testUser;
    private Article testArticle;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testArticle = new Article();
        testArticle.setId(1L);
        testArticle.setTitle("Test Article");
        testArticle.setContent("Test Content");
        testArticle.setUser(testUser);
    }

    @Test
    void createArticle_ValidData_ReturnsCreatedArticle() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

        Article result = articleService.createArticle(1L, testArticle);

        assertNotNull(result);
        assertEquals(testArticle.getId(), result.getId());
        assertEquals(testArticle.getTitle(), result.getTitle());
        verify(userRepository, times(1)).findById(1L);
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void createArticle_NullUserId_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> articleService.createArticle(null, testArticle));
    }

    @Test
    void createArticle_NullArticle_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> articleService.createArticle(1L, null));
    }

    @Test
    void createArticle_BlankTitle_ThrowsBadRequestException() {
        testArticle.setTitle("");
        assertThrows(BadRequestException.class, () -> articleService.createArticle(1L, testArticle));
    }

    @Test
    void createArticle_NullTitle_ThrowsBadRequestException() {
        testArticle.setTitle(null);
        assertThrows(BadRequestException.class, () -> articleService.createArticle(1L, testArticle));
    }

    @Test
    void createArticle_UserNotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> articleService.createArticle(1L, testArticle));
    }

    @Test
    void getAllArticles_ReturnsAllArticles() {
        List<Article> articles = Arrays.asList(testArticle);
        when(articleRepository.findAll()).thenReturn(articles);

        List<Article> result = articleService.getAllArticles();

        assertEquals(1, result.size());
        verify(articleRepository, times(1)).findAll();
    }

    @Test
    void getAllArticles_EmptyList_ReturnsEmptyList() {
        when(articleRepository.findAll()).thenReturn(Collections.emptyList());

        List<Article> result = articleService.getAllArticles();

        assertTrue(result.isEmpty());
        verify(articleRepository, times(1)).findAll();
    }

    @Test
    void getArticleById_ValidId_ReturnsArticle() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));

        Article result = articleService.getArticleById(1L);

        assertNotNull(result);
        assertEquals(testArticle.getId(), result.getId());
        verify(articleRepository, times(1)).findById(1L);
    }

    @Test
    void getArticleById_NullId_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> articleService.getArticleById(null));
    }

    @Test
    void getArticleById_NotFound_ThrowsResourceNotFoundException() {
        when(articleRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> articleService.getArticleById(1L));
    }

    @Test
    void getArticlesByUserId_ReturnsUserArticles() {
        List<Article> articles = Arrays.asList(testArticle);
        when(articleRepository.findByUserId(1L)).thenReturn(articles);

        List<Article> result = articleService.getArticlesByUserId(1L);

        assertEquals(1, result.size());
        verify(articleRepository, times(1)).findByUserId(1L);
    }

    @Test
    void getArticlesByUserId_EmptyList_ReturnsEmptyList() {
        when(articleRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

        List<Article> result = articleService.getArticlesByUserId(1L);

        assertTrue(result.isEmpty());
        verify(articleRepository, times(1)).findByUserId(1L);
    }

    @Test
    void updateArticle_ValidData_ReturnsUpdatedArticle() {
        Article updatedDetails = new Article();
        updatedDetails.setTitle("Updated Title");
        updatedDetails.setContent("Updated Content");

        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(testArticle);

        Article result = articleService.updateArticle(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Content", result.getContent());
        verify(articleRepository, times(1)).findById(1L);
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void updateArticle_PartialUpdate_ReturnsUpdatedArticle() {
        Article existingArticle = new Article();
        existingArticle.setId(1L);
        existingArticle.setTitle("Original Title");
        existingArticle.setContent("Original Content");
        existingArticle.setUser(testUser);

        Article updatedDetails = new Article();
        updatedDetails.setTitle("Updated Title");

        when(articleRepository.findById(1L)).thenReturn(Optional.of(existingArticle));
        when(articleRepository.save(any(Article.class))).thenReturn(existingArticle);

        Article result = articleService.updateArticle(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Original Content", result.getContent());
        verify(articleRepository, times(1)).findById(1L);
        verify(articleRepository, times(1)).save(any(Article.class));
    }

    @Test
    void deleteArticle_ValidId_DeletesArticle() {
        when(articleRepository.findById(1L)).thenReturn(Optional.of(testArticle));
        doNothing().when(articleRepository).delete(testArticle);

        articleService.deleteArticle(1L);

        verify(articleRepository, times(1)).findById(1L);
        verify(articleRepository, times(1)).delete(testArticle);
    }

    @Test
    void findByAuthorName_ValidName_ReturnsArticles() {
        List<Article> articles = Arrays.asList(testArticle);
        when(articleCacheByAuthor.get("Test User")).thenReturn(null);
        when(articleRepository.findByAuthorName("Test User")).thenReturn(articles);

        List<Article> result = articleService.findByAuthorName("Test User");

        assertEquals(1, result.size());
        verify(articleCacheByAuthor, times(1)).get("Test User");
        verify(articleRepository, times(1)).findByAuthorName("Test User");
        verify(articleCacheByAuthor, times(1)).put("Test User", articles);
    }

    @Test
    void findByAuthorName_CachedData_ReturnsCachedArticles() {
        List<Article> articles = Arrays.asList(testArticle);
        when(articleCacheByAuthor.get("Test User")).thenReturn(articles);

        List<Article> result = articleService.findByAuthorName("Test User");

        assertEquals(1, result.size());
        verify(articleCacheByAuthor, times(1)).get("Test User");
        verify(articleRepository, never()).findByAuthorName(anyString());
    }

    @Test
    void findByAuthorName_NullName_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> articleService.findByAuthorName(null));
    }

    @Test
    void findByAuthorName_BlankName_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> articleService.findByAuthorName(""));
    }

    @Test
    void findByAuthorName_NoArticles_ThrowsResourceNotFoundException() {
        when(articleCacheByAuthor.get("Unknown Author")).thenReturn(null);
        when(articleRepository.findByAuthorName("Unknown Author")).thenReturn(Collections.emptyList());

        assertThrows(ResourceNotFoundException.class, () -> articleService.findByAuthorName("Unknown Author"));
    }
}