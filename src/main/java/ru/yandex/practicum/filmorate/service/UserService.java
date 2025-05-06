package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.errors.validation.UserValidation;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    @Qualifier("SQL_User_Storage")
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("SQL_User_Storage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        log.info("Получение всех пользователей");
        return List.copyOf(userStorage.getAllUsers());
    }

    public User getUserById(Long id) {
        log.info("Получение пользователя с id: {}", id);
        UserValidation.nullValidation(id, "[UserService]: Запрос на получение user по ID - null",
                "UserService: user не может быть получен по ID = null") ;
        return userStorage.getUserById(id);
    }

    public List<User> getFriendsById(Long userId) {
        log.info("Получение всех друзей пользователя с id: {}", userId);
        if (userId == null || userId < 1L) {
            log.warn("UserService: Запрос на получение списка друзей по некорректному ID");
            throw new ValidationException("UserService: список друзей не может быть получен по некорректному ID: "
                    + userId);
        }

        List<User> users = userStorage.getFriendsById(userId);
        log.info("Список всех друзей пользователя успешно сформирован");
        return List.copyOf(users);
    }

    public List<User> getCommonFriends(Long userId, Long friendId) {
        log.info("Получение общих друзей пользователей с id: {} и {}", userId, friendId);
        if (userId == null || userId < 1L || friendId == null || friendId < 1L) {
            log.warn("UserService: Запрос на получение списка общих друзей c некорректным ID");
            throw new ValidationException("UserService: список общих друзей не может быть получен, ID некорректен");
        }

        Set<Long> userFriendsIds = userStorage.getUserFriendsIdsById(userId);
        Set<Long> anotherUserFriendsIds = userStorage.getUserFriendsIdsById(friendId);
        if (userFriendsIds == null || anotherUserFriendsIds == null) {
            log.warn("UserService: Не удалось получить объекты User по ID - не найдены в приложении");
            throw new NotFoundException("UserService: объекты User не найдены в приложении");
        }

        Set<Long> resultOfIntersection = userFriendsIds.stream()
                .filter(anotherUserFriendsIds::contains)
                .collect(Collectors.toSet());
        List<User> commonFriends = userStorage.getUsersByIdSet(resultOfIntersection);
        log.info("Список всех общих друзей пользователей успешно сформирован");
        return List.copyOf(commonFriends);
    }

    public User create(User user) {
        log.info("Создание нового пользователя: {}", user);
        UserValidation.validate(user);

        // Замена пустого имени на логин
        String validName;
        if (user.getName() == null || user.getName().isBlank()) {
            validName = user.getLogin();
        } else {
            validName = user.getName();
        }

        User validUser = new User(
                user.getId(),
                validName,
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
        return userStorage.create(validUser);
    }

    public User update(User newUser) {
        log.info("Обновление пользователя с id: {}", newUser.getId());
        UserValidation.validate(newUser);
        if ((newUser.getId() == null) || (newUser.getId() < 1L)) {
            log.warn("UserService: Запрос на обновление user с некорректным ID");
            throw new ValidationException("UserService: user не может быть обновлен, ID некорректен " + newUser.getId());
        }

        String validName;
        if (newUser.getName() == null || newUser.getName().isBlank()) {
            validName = newUser.getLogin();
        } else {
            validName = newUser.getName();
        }

        User validUser = new User(
                newUser.getId(),
                validName,
                newUser.getEmail(),
                newUser.getLogin(),
                newUser.getBirthday()
        );
        return userStorage.update(validUser);
    }

    public void addFriend(Long userId, Long friendId) {
        if (userId == null || userId < 1L || friendId == null || friendId < 1L) {
            log.warn("UserService: Запрос на добаление друга, ID некорректен");
            throw new ValidationException("UserService: друг не может быть добален, ID некорректен");
        }

        Set<Long> userFriendsIds = userStorage.getUserFriendsIdsById(userId);
        if (!(userFriendsIds.contains(friendId))) {
            userStorage.addFriend(userId, friendId);
            log.info("Друг успешно добавлен");
        } else {
            log.info("Друг был добавлен ранее");
        }
    }

    public void deleteFriend(Long userId, Long friendId) {
        log.info("Удаление из друзей пользователя с id: {} у пользователя с id: {}", friendId, userId);
        if (userId == null || userId < 1L || friendId == null || friendId < 1L) {
            log.warn("UserService: Запрос на удаление друга  ID = null");
            throw new ValidationException("UserService: друг не может быть удален ID = null");
        }
        // check Db
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        Set<Long> userFriendsIds = userStorage.getUserFriendsIdsById(userId);
        if (userFriendsIds.contains(friendId)) {
            userStorage.deleteFriend(userId, friendId);
            log.info("Друг успешно удален");
        } else {
            log.info("друг не может быть удален - отсутствует в списке друзей");
        }
    }
}