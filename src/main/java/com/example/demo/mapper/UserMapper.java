package com.example.demo.mapper;

import com.example.demo.dto.ArticleDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.User;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс-mapper для преобразования сущности {@link User} в DTO {@link UserDTO}.
 * Предоставляет статические методы для конвертации данных.
 */
public class UserMapper {

    private UserMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Преобразует сущность {@link User} в {@link UserDTO}.
     * Включает данные о пользователе, его статьях, подписках и подписчиках.
     *
     * @param user сущность пользователя, которую необходимо преобразовать
     * @return объект {@link UserDTO}, содержащий данные пользователя
     */
    @SuppressWarnings("checkstyle:AbbreviationAsWordInName")
    public static UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());


        List<String> subscriptions = user.getSubscriptions()
                .stream()
                .map(User::getName)
                .collect(Collectors.toList());
        dto.setSubscriptions(subscriptions);


        List<String> subscribers = user.getSubscribers()
                .stream()
                .map(User::getName)
                .collect(Collectors.toList());
        dto.setSubscribers(subscribers);


        List<ArticleDTO> articles = user.getArticles()
                .stream()
                .map(ArticleMapper::toDTO)
                .collect(Collectors.toList());
        dto.setArticles(articles);

        return dto;
    }
}