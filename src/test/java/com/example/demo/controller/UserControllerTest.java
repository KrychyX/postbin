package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
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
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_ValidData_ReturnsUserDTO() {
        // Arrange
        User user = new User();
        user.setName("Test User");
        User savedUser = new User();
        savedUser.setId(1L);
        when(userService.createUser(any(User.class))).thenReturn(savedUser);

        // Act
        UserDTO result = userController.createUser(user);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userService, times(1)).createUser(user);
    }

    @Test
    void getAllUsers_ReturnsListOfUserDTOs() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        User user2 = new User();
        user2.setId(2L);
        when(userService.getAllUsers()).thenReturn(Arrays.asList(user1, user2));

        // Act
        List<UserDTO> result = userController.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUserById_ValidId_ReturnsUserDTO() {
        // Arrange
        User user = new User();
        user.setId(1L);
        when(userService.getUserById(1L)).thenReturn(user);

        // Act
        UserDTO result = userController.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void updateUser_ValidData_ReturnsUpdatedUserDTO() {
        // Arrange
        User userDetails = new User();
        userDetails.setName("Updated Name");
        User updatedUser = new User();
        updatedUser.setId(1L);
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(updatedUser);

        // Act
        UserDTO result = userController.updateUser(1L, userDetails);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userService, times(1)).updateUser(1L, userDetails);
    }

    @Test
    void deleteUser_ValidId_CallsService() {
        // Act
        userController.deleteUser(1L);

        // Assert
        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void subscribe_ValidIds_CallsService() {
        // Act
        userController.subscribe(1L, 2L);

        // Assert
        verify(userService, times(1)).addSubscription(1L, 2L);
    }

    @Test
    void getSubscriptions_ValidUserId_ReturnsListOfUserDTOs() {
        // Arrange
        User user = new User();
        user.setId(1L);
        User subscription1 = new User();
        subscription1.setId(2L);
        User subscription2 = new User();
        subscription2.setId(3L);
        user.getSubscriptions().addAll(Arrays.asList(subscription1, subscription2));
        when(userService.getUserById(1L)).thenReturn(user);

        // Act
        List<UserDTO> result = userController.getSubscriptions(1L);

        // Assert
        assertEquals(2, result.size());
        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void createUsersBulk_ValidData_ReturnsListOfUserDTOs() {
        // Arrange
        User user1 = new User();
        user1.setName("User 1");
        User user2 = new User();
        user2.setName("User 2");
        List<User> users = Arrays.asList(user1, user2);

        User savedUser1 = new User();
        savedUser1.setId(1L);
        User savedUser2 = new User();
        savedUser2.setId(2L);
        when(userService.createUser(eq(user1))).thenReturn(savedUser1);
        when(userService.createUser(eq(user2))).thenReturn(savedUser2);

        // Act
        List<UserDTO> result = userController.createUsersBulk(users);

        // Assert
        assertEquals(2, result.size());
        verify(userService, times(2)).createUser(any(User.class));
    }
}