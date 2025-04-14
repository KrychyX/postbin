package com.example.demo.repository;

import com.example.demo.model.Article;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Репозиторий для работы с сущностями {@link Article}.
 * Предоставляет методы для выполнения операций с базой данных, связанных со статьями.
 * Расширяет {@link JpaRepository} для использования стандартных методов Spring Data JPA.
 */
public interface ArticleRepository extends JpaRepository<Article, Long> {

    /**
     * Находит все статьи, написанные автором с указанным именем.
     * Запрос выполняет JOIN между таблицами статей и пользователей,
     * чтобы найти статьи, автором которых является пользователь с именем {@code authorName}.
     *
     * @param authorName имя автора, по которому осуществляется поиск
     * @return список статей, написанных указанным автором
     */
    @Query(
            value = "SELECT a.* FROM article a "
                    + "JOIN \"users\" u ON a.user_id = u.id "
                    + "WHERE u.name = :authorName",
            nativeQuery = true
    )
    List<Article> findByAuthorName(@Param("authorName") String authorName);

    /**
     * Находит все статьи, связанные с указанным пользователем.
     *
     * @param userId идентификатор пользователя
     * @return список статей, созданных пользователем с указанным идентификатором
     */
    List<Article> findByUserId(Long userId);
}
