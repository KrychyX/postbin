package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
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
 * Контроллер для управления пользователями.
 * Предоставляет REST API для создания, получения, обновления и удаления пользователей,
 * а также для управления подписками пользователей.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    /**
     * Конструктор для внедрения зависимости {@link UserService}.
     *
     * @param userService сервис для работы с пользователями
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Создает нового пользователя.
     *
     * @param user данные пользователя
     * @return DTO созданного пользователя
     */
    @PostMapping
    public UserDTO createUser(@RequestBody User user) {
        User createdUser = userService.createUser(user);
        return UserMapper.toDTO(createdUser);
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список DTO пользователей
     */
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return DTO пользователя
     */
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return UserMapper.toDTO(user);
    }

    /**
     * Обновляет данные пользователя по его идентификатору.
     *
     * @param id          идентификатор пользователя
     * @param userDetails  новые данные пользователя
     * @return DTO обновленного пользователя
     */
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return UserMapper.toDTO(updatedUser);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     */
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    /**
     * Добавляет подписку пользователя на другого пользователя.
     *
     * @param subscriberId идентификатор пользователя, который подписывается
     * @param channelId    идентификатор пользователя, на которого подписываются
     */
    @PostMapping("/{subscriberId}/subscribe/{channelId}")
    public void subscribe(
            @PathVariable Long subscriberId,
            @PathVariable Long channelId
    ) {
        userService.addSubscription(subscriberId, channelId);
    }

    /**
     * Возвращает список подписок пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список DTO пользователей, на которых подписан текущий пользователь
     */
    @GetMapping("/{userId}/subscriptions")
    public List<UserDTO> getSubscriptions(@PathVariable Long userId) {
        User user = userService.getUserById(userId);
        return user.getSubscriptions()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }
}