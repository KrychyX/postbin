package com.example.demo.thing;

import lombok.Getter;
import lombok.Setter;

/**
 * Класс {@code Article} представляет статью, которая содержит заголовок и содержание.
 * Используется для хранения и управления данными, связанными с текстовыми статьями.
 */
@Getter
@Setter
public class Article {

    private String title;
    private String content;

    /**
     * Создает новый экземпляр статьи с указанным заголовком и содержанием.
     *
     * @param title   Заголовок статьи.
     * @param content Содержание статьи.
     */
    public Article(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
