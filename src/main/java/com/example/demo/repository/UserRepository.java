package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с сущностями {@link User}.
 * Предоставляет методы для выполнения операций с базой данных, связанных с пользователями.
 * Расширяет {@link JpaRepository} для использования стандартных методов Spring Data JPA.
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
