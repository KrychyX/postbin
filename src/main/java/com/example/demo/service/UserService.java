package com.example.demo.service;

import com.example.demo.exception.BadRequestException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с пользователями.
 * Предоставляет методы для создания, получения, обновления и удаления пользователей,
 * а также для управления подписками пользователей.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    /**
     * Конструктор для внедрения зависимости {@link UserRepository}.
     *
     * @param userRepository репозиторий для работы с пользователями
     */
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Создает нового пользователя.
     *
     * @param user данные пользователя
     * @return сохраненный пользователь
     */
    @Transactional
    public User createUser(User user) {
        logger.info("Попытка создания пользователя с email: {}", user.getEmail());
        if (user == null || user.getName() == null || user.getName().isBlank()) {
            logger.error("Имя пользователя обязательно");
            throw new BadRequestException("Имя пользователя обязательно");
        }
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            logger.error("Некорректный формат email: {}", user.getEmail());
            throw new BadRequestException("Требуется корректный email");
        }

        User savedUser = userRepository.save(user);
        logger.info("Пользователь успешно создан с ID: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Возвращает всех пользователей.
     */
    public List<User> getAllUsers() {
        logger.info("Получение списка всех пользователей");
        return userRepository.findAll();
    }

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return найденный пользователь
     * @throws RuntimeException если пользователь не найден
     */
    public User getUserById(Long id) {
        logger.info("Получение пользователя по ID: {}", id);
        if (id == null) {
            logger.error("ID пользователя не может быть null");
            throw new BadRequestException("ID пользователя не может быть null");
        }
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", id);
                    return new ResourceNotFoundException("Пользователь с ID " + id + " не найден");
                });
    }

    /**
     * Обновляет данные пользователя по его идентификатору.
     *
     * @param id          идентификатор пользователя
     * @param userDetails новые данные пользователя
     * @return обновленный пользователь
     */
    @Transactional
    public User updateUser(Long id, User userDetails) {
        logger.info("Обновление пользователя с ID: {}", id);
        User user = getUserById(id);

        if (userDetails.getName() != null) {
            logger.debug("Обновление имени для пользователя с ID: {}", id);
            user.setName(userDetails.getName());
        }
        if (userDetails.getEmail() != null) {
            if (!userDetails.getEmail().contains("@")) {
                logger.error("Некорректный формат email: {}", userDetails.getEmail());
                throw new BadRequestException("Некорректный формат email");
            }
            logger.debug("Обновление email для пользователя с ID: {}", id);
            user.setEmail(userDetails.getEmail());
        }

        User updatedUser = userRepository.save(user);
        logger.info("Пользователь с ID {} успешно обновлен", id);
        return updatedUser;
    }

    /**
     * Удаляет пользователя по его идентификатору.
     * Также удаляет все связанные подписки и подписчиков.
     *
     * @param id идентификатор пользователя
     */
    @Transactional
    public void deleteUser(Long id) {
        logger.info("Удаление пользователя с ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Пользователь с ID {} не найден", id);
                    return new RuntimeException("Пользователь с ID " + id + " не найден");
                });

        logger.debug("Очистка подписок для пользователя с ID: {}", id);
        for (User subscriber : user.getSubscribers()) {
            subscriber.getSubscriptions().remove(user);
        }

        for (User subscription : user.getSubscriptions()) {
            subscription.getSubscribers().remove(user);
        }

        user.getSubscribers().clear();
        user.getSubscriptions().clear();

        userRepository.save(user);
        userRepository.delete(user);
        logger.info("Пользователь с ID {} успешно удален", id);
    }

    /**
     * Добавляет подписку пользователя на другого пользователя.
     *
     * @param subscriberId идентификатор пользователя, который подписывается
     * @param channelId    идентификатор пользователя, на которого подписываются
     * @throws RuntimeException если пользователь или канал не найдены
     */
    @Transactional
    public void addSubscription(Long subscriberId, Long channelId) {
        logger.info("Добавление подписки от пользователя {} на пользователя {}",
                subscriberId, channelId);
        if (subscriberId == null || channelId == null) {
            logger.error("ID подписчика и канала не могут быть null");
            throw new BadRequestException("ID подписчика и канала не могут быть null");
        }
        if (subscriberId.equals(channelId)) {
            logger.error("Пользователь {} пытается подписаться на самого себя", subscriberId);
            throw new BadRequestException("Пользователь не может подписаться на самого себя");
        }

        User subscriber = getUserById(subscriberId);
        User channel = getUserById(channelId);

        if (subscriber.getSubscriptions().contains(channel)) {
            logger.error("Подписка от {} на {} уже существует", subscriberId, channelId);
            throw new BadRequestException("Подписка уже существует");
        }

        subscriber.getSubscriptions().add(channel);
        userRepository.save(subscriber);
        logger.info("Подписка от {} на {} успешно добавлена", subscriberId, channelId);
    }
}