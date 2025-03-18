package com.example.demo.repository;

import com.example.demo.model.Article;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с сущностями {@link Article}.
 * Предоставляет методы для выполнения операций с базой данных, связанных со статьями.
 * Расширяет {@link JpaRepository} для использования стандартных методов Spring Data JPA.
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * Находит все статьи, связанные с указанным пользователем.
     *
     * @param userId идентификатор пользователя
     * @return список статей, созданных пользователем с указанным идентификатором
     */
    List<Article> findByUserId(Long userId);
}
