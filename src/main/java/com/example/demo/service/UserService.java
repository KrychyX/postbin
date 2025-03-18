package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Сервис для работы с пользователями.
 * Предоставляет методы для создания, получения, обновления и удаления пользователей,
 * а также для управления подписками пользователей.
 */
@Service
public class UserService {

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
    public User createUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
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
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    /**
     * Обновляет данные пользователя по его идентификатору.
     *
     * @param id          идентификатор пользователя
     * @param userDetails новые данные пользователя
     * @return обновленный пользователь
     */
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        user.setName(userDetails.getName());
        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     * Также удаляет все связанные подписки и подписчиков.
     *
     * @param id идентификатор пользователя
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));


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

        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new RuntimeException(
                        "Subscriber not found with id: " + subscriberId));

        User channel = userRepository.findById(channelId)
                .orElseThrow(() -> new RuntimeException(
                        "Channel not found with id: " + channelId));

        subscriber.getSubscriptions().add(channel);
        userRepository.save(subscriber);
    }
}