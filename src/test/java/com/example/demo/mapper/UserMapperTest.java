package com.example.demo.mapper;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.Article;
import com.example.demo.model.User;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toDTO_WithValidUser_ReturnsCorrectDTO() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        // Create subscriptions
        User subscription1 = new User();
        subscription1.setId(2L);
        subscription1.setName("Subscription 1");
        User subscription2 = new User();
        subscription2.setId(3L);
        subscription2.setName("Subscription 2");
        user.setSubscriptions(Arrays.asList(subscription1, subscription2));

        // Create subscribers
        User subscriber1 = new User();
        subscriber1.setId(4L);
        subscriber1.setName("Subscriber 1");
        User subscriber2 = new User();
        subscriber2.setId(5L);
        subscriber2.setName("Subscriber 2");
        user.setSubscribers(Arrays.asList(subscriber1, subscriber2));

        // Create articles
        Article article1 = new Article();
        article1.setId(1L);
        article1.setTitle("Article 1");
        article1.setContent("Content 1");
        article1.setUser(user);

        Article article2 = new Article();
        article2.setId(2L);
        article2.setTitle("Article 2");
        article2.setContent("Content 2");
        article2.setUser(user);

        user.setArticles(Arrays.asList(article1, article2));

        // Act
        UserDTO dto = UserMapper.toDTO(user);

        // Assert
        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());

        // Check subscriptions
        assertEquals(2, dto.getSubscriptions().size());
        assertTrue(dto.getSubscriptions().contains("Subscription 1"));
        assertTrue(dto.getSubscriptions().contains("Subscription 2"));

        // Check subscribers
        assertEquals(2, dto.getSubscribers().size());
        assertTrue(dto.getSubscribers().contains("Subscriber 1"));
        assertTrue(dto.getSubscribers().contains("Subscriber 2"));

        // Check articles
        assertEquals(2, dto.getArticles().size());
        for (ArticleDTO articleDTO : dto.getArticles()) {
            assertEquals(user.getName(), articleDTO.getAuthor());
        }
    }

    @Test
    void toDTO_WithUserWithoutRelations_ReturnsDTOWithEmptyLists() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setSubscriptions(Collections.emptyList());
        user.setSubscribers(Collections.emptyList());
        user.setArticles(Collections.emptyList());

        // Act
        UserDTO dto = UserMapper.toDTO(user);

        // Assert
        assertNotNull(dto);
        assertEquals(user.getId(), dto.getId());
        assertEquals(user.getName(), dto.getName());
        assertEquals(user.getEmail(), dto.getEmail());
        assertTrue(dto.getSubscriptions().isEmpty());
        assertTrue(dto.getSubscribers().isEmpty());
        assertTrue(dto.getArticles().isEmpty());
    }

    @Test
    void toDTO_WithNullUser_ThrowsException() {
        assertThrows(NullPointerException.class, () -> UserMapper.toDTO(null));
    }
}