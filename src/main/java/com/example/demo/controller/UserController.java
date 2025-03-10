package com.example.demo.controller;

import com.example.demo.service.UserService;
import com.example.demo.thing.Article;
import com.example.demo.thing.User;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер {@code UserController} обрабатывает HTTP-запросы, связанные с пользователями.
 * Этот класс использует аннотацию Spring {@code @RestController}
 * для обозначения того, что он является
 * контроллером, и все методы возвращают данные в формате JSON. Также используется аннотация
 * {@code @RequestMapping} для указания базового пути {@code /api/users}.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {


    private UserService userService;

    /**
     * Конструктор для инициализации контроллера и сервиса пользователей.
     *
     * @param userService Сервис для работы с пользователями.
     */
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        initialize();
    }

    private void initialize() {

        User user1 = new User(null, "admin", "admin228@gmail.com",
                List.of(new Article("Spring", "Java is cool")));
        userService.createUser(user1);

        User user2 = new User(null, "No admin", "krychok@gmail.com",
                List.of(new Article("What", "bla bla bly")));
        userService.createUser(user2);

        User user3 = new User(null, "kirill", "krychka96@gmail.com",
                List.of(new Article("Bike", "i like motorbike")));
        userService.createUser(user3);

    }


    /**
     * Возвращает список всех пользователей.
     *
     * @return Список всех пользователей.
     */
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Возвращает пользователя по его уникальному идентификатору.
     *
     * @param id Уникальный идентификатор пользователя.
     * @return Ответ с найденным пользователем в формате JSON.
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Возвращает список пользователей, отфильтрованных по электронной почте и/или имени.
     * Если параметр {@code email} или {@code name} не указан, он игнорируется при фильтрации.
     *
     * @param email Электронная почта для фильтрации (необязательный параметр).
     * @param name  Имя для фильтрации (необязательный параметр).
     * @return Список пользователей, соответствующих критериям фильтрации.
     */
    @GetMapping("/search")
    public List<User> getUsersByEmailOrName(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name) {
        return userService.getUsersByEmailOrName(email, name);
    }
}