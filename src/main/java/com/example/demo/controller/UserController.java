package com.example.demo.controller;

import com.example.demo.dto.UserDTO;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
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
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для управления пользователями.
 * Предоставляет REST API для создания, получения, обновления и удаления пользователей,
 * а также для управления подписками пользователей.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "User Controller", description = "API для управления пользователями и их подписками")
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
    @Operation(summary = "Создать пользователя", description = "Создает нового пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно создан"),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные")
    })
    @PostMapping
    public UserDTO createUser(
            @Parameter(description = "Данные пользователя") @RequestBody @Valid User user
    ) {
        User createdUser = userService.createUser(user);
        return UserMapper.toDTO(createdUser);
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return список DTO пользователей
     */
    @Operation(summary = "Получить всех пользователей",
            description = "Возвращает список всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен")
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return DTO пользователя
     */
    @Operation(summary = "Получить пользователя по ID",
            description = "Возвращает пользователя по его идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно найден"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public UserDTO getUserById(
            @Parameter(description = "ID пользователя") @PathVariable Long id
    ) {
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
    @Operation(summary = "Обновить пользователя",
            description = "Обновляет данные пользователя по его идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлен"),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping("/{id}")
    public UserDTO updateUser(
            @Parameter(description = "ID пользователя") @PathVariable Long id,
            @Parameter(description = "Новые данные пользователя")
            @RequestBody @Valid User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return UserMapper.toDTO(updatedUser);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     */
    @Operation(summary = "Удалить пользователя",
            description = "Удаляет пользователя по его идентификатору")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователь успешно удален"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public void deleteUser(
            @Parameter(description = "ID пользователя") @PathVariable Long id
    ) {
        userService.deleteUser(id);
    }

    /**
     * Добавляет подписку пользователя на другого пользователя.
     *
     * @param subscriberId идентификатор пользователя, который подписывается
     * @param channelId    идентификатор пользователя, на которого подписываются
     */
    @Operation(summary = "Добавить подписку",
            description = "Добавляет подписку пользователя на другого пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Подписка успешно добавлена"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
        @ApiResponse(responseCode = "400", description = "Невозможно подписаться на самого себя")
    })
    @PostMapping("/{subscriberId}/subscribe/{channelId}")
    public void subscribe(
            @Parameter(description = "ID подписчика") @PathVariable Long subscriberId,
            @Parameter(description = "ID канала") @PathVariable Long channelId
    ) {
        userService.addSubscription(subscriberId, channelId);
    }

    /**
     * Возвращает список подписок пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список DTO пользователей, на которых подписан текущий пользователь
     */
    @Operation(summary = "Получить подписки",
            description = "Возвращает список подписок пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Список подписок успешно получен"),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{userId}/subscriptions")
    public List<UserDTO> getSubscriptions(
            @Parameter(description = "ID пользователя") @PathVariable Long userId
    ) {
        User user = userService.getUserById(userId);
        return user.getSubscriptions()
                .stream()
                .map(UserMapper::toDTO)
                .toList();
    }

    /**
     * Создает нескольких пользователей.
     *
     * @param users список пользователей
     * @return список DTO созданных пользователей
     */
    @Operation(summary = "Создать нескольких пользователей",
            description = "Создает нескольких пользователей")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Пользователи успешно созданы"),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные")
    })
    @PostMapping("/bulk")
    public List<UserDTO> createUsersBulk(
            @Parameter(description = "Список пользователей") @RequestBody @Valid List<User> users
    ) {
        return users.stream()
                .map(userService::createUser)
                .map(UserMapper::toDTO)
                .toList();
    }
}