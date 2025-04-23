package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;

@Service
public class UserService {

    @Qualifier("SQL_User_Storage")
    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.userStorage = inMemoryUserStorage;
    }

    public Collection<User> findAll() {
        if (userStorage.findAll().isEmpty()) {
            throw new NotFoundException("Нет активных пользователей");
        }
        return userStorage.findAll();
    }

    public User get(Long id) {
        errorOfUserExist(id);
        return userStorage.getUser(id);
    }

    public Collection<Long> findAllFriends(Long id) {
        errorOfUserExist(id);
        return userStorage.findAllFriends(id);
    }

    public Collection<Long> getCommonFriends(Long id, Long friendId) {
        errorOfUserExist(id);
        errorOfUserExist(friendId);
        return userStorage.getCommonFriends(id, friendId);
    }

    public User create(User user) {
        validateUser(user);
        if (userStorage.findAll().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        userStorage.create(user);
        return user;
    }

    public User update(User newUser) {
        errorOfUserExist(newUser.getId());
        validateUser(newUser);
        return userStorage.update(newUser);
    }

    public long addFriend(Long id, Long friendId) {
        errorOfUserExist(id);
        errorOfUserExist(friendId);
        return userStorage.addFriend(id, friendId);
    }

    public boolean deleteFriend(Long id, Long friendId) {
        errorOfUserExist(id);
        errorOfUserExist(friendId);
        return userStorage.deleteFriend(id, friendId);
    }

    private void validateUser(User user) {
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
        if (!userStorage.findAllKeys().contains(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }
}
