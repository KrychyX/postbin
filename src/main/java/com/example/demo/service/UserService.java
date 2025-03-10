package com.example.demo.service;

import com.example.demo.thing.User;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

/**
 * Сервисный класс {@code UserService} предоставляет методы для управления пользователями.
 * Этот класс использует аннотацию Spring {@code @Service}
 * , чтобы быть зарегистрированным как компонент
 * в контексте Spring. Он хранит список пользователей в памяти и предоставляет методы для создания,
 * поиска и фильтрации пользователей.
 */
@Service
public class UserService {

    private final List<User> users = new ArrayList<User>();
    private final AtomicLong counter = new AtomicLong();

    /**
     * Создает нового пользователя и добавляет его в список.
     * апрол
     * Уникальный идентификатор пользователя генерируется автоматически.
     *
     * @param user Пользователь, которого необходимо создать.
     */
    public void createUser(User user) {
        user.setId(counter.incrementAndGet());
        users.add(user);
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return Список всех пользователей.
     */
    public List<User> getAllUsers() {
        return users;
    }

    /**
     * Возвращает пользователя по его уникальному идентификатору.
     *
     * @param id Уникальный идентификатор пользователя.
     * @return Найденный пользователь.
     * @throws RuntimeException если пользователь с указанным идентификатором не найден.
     */
    public User getUserById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Возвращает список пользователей, отфильтрованных по электронной почте и/или имени.
     * Если параметр {@code email} или {@code name} равен {@code null}
     * он игнорируется при фильтрации.
     *
     * @param email Электронная почта для фильтрации (может быть {@code null}).
     * @param name  Имя для фильтрации (может быть {@code null}).
     * @return Список пользователей, соответствующих критериям фильтрации.
     */
    public List<User> getUsersByEmailOrName(String email, String name) {
        return users.stream()
                .filter(user -> (email == null || user.getEmail().equals(email))
                        && (name == null || user.getName().equals(name))).toList();
    }
}
