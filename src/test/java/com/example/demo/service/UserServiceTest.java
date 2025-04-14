package com.example.demo.service;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        anotherUser = new User();
        anotherUser.setId(2L);
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");
    }

    @Test
    void createUser_ValidData_ReturnsCreatedUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser(testUser);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_NullName_ThrowsBadRequestException() {
        testUser.setName(null);
        assertThrows(BadRequestException.class, () -> userService.createUser(testUser));
    }

    @Test
    void createUser_BlankName_ThrowsBadRequestException() {
        testUser.setName("");
        assertThrows(BadRequestException.class, () -> userService.createUser(testUser));
    }

    @Test
    void createUser_NullEmail_ThrowsBadRequestException() {
        testUser.setEmail(null);
        assertThrows(BadRequestException.class, () -> userService.createUser(testUser));
    }

    @Test
    void createUser_InvalidEmail_ThrowsBadRequestException() {
        testUser.setEmail("invalid-email");
        assertThrows(BadRequestException.class, () -> userService.createUser(testUser));
    }

    @Test
    void getAllUsers_ReturnsAllUsers() {
        List<User> users = Arrays.asList(testUser, anotherUser);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsers_EmptyList_ReturnsEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<User> result = userService.getAllUsers();

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_ValidId_ReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_NullId_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> userService.getUserById(null));
    }

    @Test
    void getUserById_NotFound_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_ValidData_ReturnsUpdatedUser() {
        User updatedDetails = new User();
        updatedDetails.setName("Updated Name");
        updatedDetails.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.updateUser(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("updated@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_PartialUpdate_ReturnsUpdatedUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("Original Name");
        existingUser.setEmail("original@example.com");

        User updatedDetails = new User();
        updatedDetails.setName("Updated Name");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User result = userService.updateUser(1L, updatedDetails);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("original@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_InvalidEmail_ThrowsBadRequestException() {
        User updatedDetails = new User();
        updatedDetails.setEmail("invalid-email");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        assertThrows(BadRequestException.class, () -> userService.updateUser(1L, updatedDetails));
    }

    @Test
    void deleteUser_ValidId_DeletesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(testUser);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void addSubscription_ValidIds_AddsSubscription() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        userService.addSubscription(1L, 2L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).findById(2L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void addSubscription_NullSubscriberId_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> userService.addSubscription(null, 2L));
    }

    @Test
    void addSubscription_NullChannelId_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> userService.addSubscription(1L, null));
    }

    @Test
    void addSubscription_SameIds_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> userService.addSubscription(1L, 1L));
    }

    @Test
    void addSubscription_AlreadySubscribed_ThrowsBadRequestException() {
        testUser.getSubscriptions().add(anotherUser);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findById(2L)).thenReturn(Optional.of(anotherUser));

        assertThrows(BadRequestException.class, () -> userService.addSubscription(1L, 2L));
    }
}