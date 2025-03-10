package com.example.demo.thing;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Класс {@code User} представляет пользователя, который имеет идентификатор, имя, электронную почту
 * и связанную статью.
 */
@Getter
@Setter
public class User {

    private Long id;
    private String name;
    private String email;
    private List<Article> articles;

    /**
     * Создает нового пользователя с указанными параметрами.
     *
     * @param id       Уникальный идентификатор пользователя.
     * @param name     Имя пользователя.
     * @param email    Электронная почта пользователя.
     * @param articles Статья, связанная с пользователем.
     */
    public User(Long id, String name, String email, List<Article> articles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.articles = articles;
    }
}
