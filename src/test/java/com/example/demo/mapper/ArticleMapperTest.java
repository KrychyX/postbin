package com.example.demo.mapper;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.model.Article;
import com.example.demo.model.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ArticleMapperTest {

    @Test
    void toDTO_WithValidArticle_ReturnsCorrectDTO() {
        // Arrange
        User author = new User();
        author.setId(1L);
        author.setName("Test Author");
        author.setEmail("author@example.com");

        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Title");
        article.setContent("Test Content");
        article.setUser(author);

        // Act
        ArticleDTO dto = ArticleMapper.toDTO(article);

        // Assert
        assertNotNull(dto);
        assertEquals(article.getId(), dto.getId());
        assertEquals(article.getTitle(), dto.getTitle());
        assertEquals(article.getContent(), dto.getContent());
        assertEquals(author.getName(), dto.getAuthor());
    }

    @Test
    void toDTO_WithNullArticle_ThrowsException() {
        assertThrows(NullPointerException.class, () -> ArticleMapper.toDTO(null));
    }

    @Test
    void toDTO_WithArticleWithoutUser_ThrowsException() {
        Article article = new Article();
        article.setId(1L);
        article.setTitle("Test Title");
        article.setContent("Test Content");

        assertThrows(NullPointerException.class, () -> ArticleMapper.toDTO(article));
    }
}