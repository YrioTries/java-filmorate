package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    @Qualifier("SQL_User_Storage")
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("SQL_User_Storage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> findAll() {
        log.info("Получение всех пользователей");
        if (userStorage.findAll().isEmpty()) {
            throw new NotFoundException("Нет активных пользователей");
        }
        return userStorage.findAll();
    }

    public User get(Long id) {
        log.info("Получение пользователя с id: {}", id);
        errorOfUserExist(id);
        return userStorage.getUser(id);
    }

    public Collection<Long> findAllFriends(Long id) {
        log.info("Получение всех друзей пользователя с id: {}", id);
        errorOfUserExist(id);
        return userStorage.findAllFriends(id);
    }

    public Collection<Long> getCommonFriends(Long id, Long friendId) {
        log.info("Получение общих друзей пользователей с id: {} и {}", id, friendId);
        errorOfUserExist(id);
        errorOfUserExist(friendId);
        return userStorage.getCommonFriends(id, friendId);
    }

    public User create(User user) {
        log.info("Создание нового пользователя: {}", user);
        validateUser(user);
        if (userStorage.findAll().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))) {
            throw new ValidationException("Этот имейл уже используется");
        }
        userStorage.create(user);
        return user;
    }

    public User update(User newUser) {
        log.info("Обновление пользователя с id: {}", newUser.getId());
        errorOfUserExist(newUser.getId());
        validateUser(newUser);
        return userStorage.update(newUser);
    }

    public long addFriend(Long userId, Long friendId) {
        log.info("Добавление в друзья пользователя с id: {} пользователю с id: {}", friendId, userId);
        validateUserExists(userId);
        validateUserExists(friendId);

        Optional<Long> currentStatus = getFriendshipStatus(userId, friendId);

        if (currentStatus.isPresent() && currentStatus.get().equals(1L)) {
            updateFriendshipStatus(userId, friendId, 2L);
            userStorage.addFriendship(friendId, userId, 2L);
        } else {
            userStorage.addFriendship(userId, friendId, 1L);
        }

        return friendId;
    }

    private Optional<Long> getFriendshipStatus(Long userId, Long friendId) {
        log.debug("Получение статуса дружбы между пользователями с id: {} и {}", userId, friendId);
        return userStorage.getFriendshipStatus(userId, friendId);
    }

    private void updateFriendshipStatus(Long userId, Long friendId, Long statusId) {
        log.debug("Обновление статуса дружбы между пользователями с id: {} и {} на статус {}",
                userId, friendId, statusId);
        userStorage.updateFriendshipStatus(userId, friendId, statusId);
    }

    private void validateUserExists(Long userId) {
        log.debug("Проверка существования пользователя с id: {}", userId);
        if (!userStorage.findAllKeys().contains(userId)) {
            throw new NotFoundException("Пользователь с id = " + userId + " не найден");
        }
    }

    public boolean deleteFriend(Long id, Long friendId) {
        log.info("Удаление из друзей пользователя с id: {} у пользователя с id: {}", friendId, id);
        errorOfUserExist(id);
        errorOfUserExist(friendId);
        return userStorage.deleteFriend(id, friendId);
    }

    private void validateUser(User user) {
        log.debug("Валидация пользователя: {}", user);
        if (user.getLogin() == null || user.getLogin().isEmpty()) {
            throw new ValidationException("Некорректный логин пользователя");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный имейл пользователя");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректная дата рождения пользователя");
        }
    }

    private void errorOfUserExist(Long id) {
        log.debug("Проверка существования пользователя с id: {}", id);
        if (!userStorage.findAllKeys().contains(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }
}