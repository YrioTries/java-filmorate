package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ValidationTool;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    private final static String PROGRAM_LEVEL = "UserService";

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAllUsers() {
        return List.copyOf(userStorage.getAllUsers());
    }

    public User getUserById(Long id) {
        ValidationTool.checkForNull(id, PROGRAM_LEVEL, "User не может быть получен по ID = null");

        User user = userStorage.getUserById(id);

        log.info(PROGRAM_LEVEL + ": Объект user успешно найден по ID");
        return user;
    }

    public User create(User user) {
        ValidationTool.userCheck(user, PROGRAM_LEVEL);

        String validUserName;
        if (user.getName() == null || user.getName().isBlank()) {
            validUserName = user.getLogin();
        } else {
            validUserName = user.getName();
        }

        User validUser = new User(
                user.getId(),
                validUserName,
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
        return userStorage.create(validUser);
    }

    public User update(User user) {
        ValidationTool.userCheck(user, PROGRAM_LEVEL);

        ValidationTool.checkId(user.getId(), PROGRAM_LEVEL, "user не может быть обновлен, некорректный id:"
                + user.getId());

        getUserById(user.getId());

        String validUserName;
        if (user.getName() == null || user.getName().isBlank()) {
            validUserName = user.getLogin();
        } else {
            validUserName = user.getName();
        }

        User validUser = new User(
                user.getId(),
                validUserName,
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
        return userStorage.update(validUser);
    }

    public void addFriend(Long userId, Long friendId) {
        ValidationTool.checkId(userId, friendId, PROGRAM_LEVEL, "Запрос на добавление друга, ID некорректен");

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        Set<Long> userFriendsIdsSet = userStorage.getUserFriendsIdsById(userId);
        if (!(userFriendsIdsSet.contains(friendId))) {
            userStorage.addFriend(userId, friendId);
            log.info("Друг успешно добавлен");
        } else {
            log.info("Друг был добавлен ранее");
        }
    }

    public void removeFriend(Long userId, Long friendId) {

        ValidationTool.checkId(userId, friendId, PROGRAM_LEVEL, "Друг не может быть удален ID = null");

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        Set<Long> userFriendsIds = userStorage.getUserFriendsIdsById(userId);
        if (userFriendsIds.contains(friendId)) {
            userStorage.removeFriend(userId, friendId);
            log.info("Друг успешно удален");
        } else {
            log.info("друг не может быть удален - отсутствует в списке друзей");
        }
    }

    public List<User> getAllFriendsById(Long userId) {
        ValidationTool.checkId(userId, PROGRAM_LEVEL, "Cписок друзей не может быть получен по некорректному ID:"
                + userId);

        userStorage.getUserById(userId);

        List<User> users = userStorage.getAllFriendsById(userId);
        log.info("Список всех друзей пользователя успешно создан");
        return List.copyOf(users);
    }

    public List<User> getAllCommonFriendsByIds(Long userId, Long anotherUserId) {
        ValidationTool.checkId(userId, anotherUserId, PROGRAM_LEVEL, "Cписок общих друзей " +
                "не может быть получен, ID некорректен");

        userStorage.getUserById(userId);
        userStorage.getUserById(anotherUserId);

        Set<Long> setOfUserFriendsIds = userStorage.getUserFriendsIdsById(userId);
        Set<Long> setOfAnotherUserFriendsIds = userStorage.getUserFriendsIdsById(anotherUserId);


        if (setOfUserFriendsIds == null || setOfAnotherUserFriendsIds == null) {
            log.warn("UserService: Не удалось получить объекты User по ID - не найдены в приложении");
            throw new NotFoundException("UserService: объекты User не найдены в приложении");
        }

        Set<Long> resultOfIntersection = setOfUserFriendsIds.stream()
                .filter(setOfAnotherUserFriendsIds::contains)
                .collect(Collectors.toSet());
        List<User> commonFriends = userStorage.getUsersByIdSet(resultOfIntersection);
        log.info("Список всех общих друзей пользователей успешно создан");
        return List.copyOf(commonFriends);
    }
}
