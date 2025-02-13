package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {

    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public Collection<Long> findAllFriends(Long id) {
        errorOfUserExist(id);
        return users.get(id).getFriends();
    }

    public Collection<Long> getCommonFriends(Long id, Long friendId) {
        errorOfUserExist(id);
        errorOfUserExist(friendId);

        User user = users.get(id);
        User friendUser = users.get(friendId);

        Set<Long>commonFriendSet = new TreeSet<>(user.getFriends());
        commonFriendSet.retainAll(friendUser.getFriends());

        return commonFriendSet;
    }



    public User create(User user) {
        validateUser(user);
        if (users.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    public User addFriend(Long id, Long friendId) {
        errorOfUserExist(id);
        errorOfUserExist(friendId);

        User user = users.get(id);
        User friendUser = users.get(friendId);

        Set<Long>friendSet = new TreeSet<>();
        friendSet = user.getFriends();
        friendSet.add(friendId);
        user.setFriends(friendSet);
        update(user);

        friendSet = friendUser.getFriends();
        friendSet.add(id);
        friendUser.setFriends(friendSet);
        update(friendUser);

        return friendUser;
    }

    public User update(User newUser) {
        // Проверяем, существует ли пользователь с указанным ID
        errorOfUserExist(newUser.getId());

        User oldUser = users.get(newUser.getId());
        validateUser(newUser);

        // Обновляем другие поля, если они указаны
        oldUser.setLogin(newUser.getLogin());

        if (oldUser.getName() == null || oldUser.getName().isBlank()) {
            oldUser.setName(oldUser.getLogin());
        } else {
            oldUser.setName(newUser.getName());
        }

        oldUser.setEmail(newUser.getEmail());
        oldUser.setBirthday(newUser.getBirthday());

        // Возвращаем обновленного пользователя
        return oldUser;
    }

    public Long deleteFriend(Long id, Long friendId) {
        errorOfUserExist(id);
        errorOfUserExist(friendId);

        User user = users.get(id);
        User friendUser = users.get(friendId);

        Set<Long>friendSet = new TreeSet<>();
        friendSet = user.getFriends();
        friendSet.remove(friendId);
        user.setFriends(friendSet);
        update(user);

        friendSet = friendUser.getFriends();
        friendSet.remove(id);
        friendUser.setFriends(friendSet);
        update(friendUser);

        return friendId;
    }

    private void validateUser(User user) {
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            throw new ValidationException("Некорректный логин пользователя");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()
                || !user.getEmail().contains("@")) {
            throw new ValidationException("Некорректный имейл пользователя");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Некорректный дата рождения пользователя");
        }
    }

    private void errorOfUserExist(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
    }

    // Вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
