package ru.yandex.practicum.filmorate.storage.dao.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
@Qualifier("InMemoryUserStorage")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users;

    public InMemoryUserStorage() {
        users = new HashMap<>();
    }

    @Override
    public Collection<User> findAll() {
        log.info("Получение всех пользователей из памяти");
        return users.values();
    }

    @Override
    public Collection<Long> findAllKeys() {
        log.info("Получение всех ключей пользователей из памяти");
        return users.keySet();
    }

    @Override
    public User getUser(Long id) {
        log.info("Получение пользователя с id: {} из памяти", id);
        return users.get(id);
    }

    @Override
    public Collection<Long> findAllFriends(Long id) {
        log.info("Получение всех друзей пользователя с id: {} из памяти", id);
        return users.getOrDefault(id, new User()).getFriends();
    }

    @Override
    public Set<Long> getCommonFriends(Long id, Long friendId) {
        log.info("Получение общих друзей пользователей с id: {} и {}", id, friendId);
        Set<Long> friendsOfUser1 = new HashSet<>(users.getOrDefault(id, new User()).getFriends());
        Set<Long> friendsOfUser2 = new HashSet<>(users.getOrDefault(friendId, new User()).getFriends());
        friendsOfUser1.retainAll(friendsOfUser2);
        return friendsOfUser1;
    }

    @Override
    public User create(User user) {
        log.info("Создание нового пользователя: {}", user);
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Создан новый пользователь с id: {}", user.getId());
        return user;
    }

    @Override
    public User update(User newUser) {
        log.info("Обновление пользователя с id: {}", newUser.getId());
        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName() != null && !newUser.getName().isBlank() ? newUser.getName() : newUser.getLogin());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setBirthday(newUser.getBirthday());

        users.put(oldUser.getId(), oldUser);
        log.info("Пользователь с id: {} обновлен", newUser.getId());
        return oldUser;
    }

    @Override
    public void addFriendship(Long userId, Long friendId, Long statusId) {
        log.info("Добавление дружбы между пользователями с id: {} и {}", userId, friendId);
        User user = users.get(userId);
        User friendUser = users.get(friendId);

        if (user != null && friendUser != null) {
            user.getFriends().add(friendId);
            friendUser.getFriends().add(userId);

            users.put(userId, user);
            users.put(friendId, friendUser);
            log.info("Дружба между пользователями с id: {} и {} добавлена", userId, friendId);
        }
    }

    @Override
    public void removeFriendship(Long userId, Long friendId) {
        log.info("Удаление дружбы между пользователями с id: {} и {}", userId, friendId);
        User user = users.get(userId);
        User friendUser = users.get(friendId);

        if (user != null && friendUser != null) {
            user.getFriends().remove(friendId);
            friendUser.getFriends().remove(userId);

            users.put(userId, user);
            users.put(friendId, friendUser);
            log.info("Дружба между пользователями с id: {} и {} удалена", userId, friendId);
        }
    }

    @Override
    public Optional<Long> getFriendshipStatus(Long userId, Long friendId) {
        log.info("Получение статуса дружбы между пользователями с id: {} и {}", userId, friendId);
        User user = users.get(userId);
        if (user != null && user.getFriends().contains(friendId)) {
            return Optional.of(1L); // Предположим, что статус дружбы всегда 1 (запрос на дружбу)
        }
        return Optional.empty();
    }

    @Override
    public void updateFriendshipStatus(Long userId, Long friendId, Long statusId) {
        log.info("Обновление статуса дружбы между пользователями с id: {} и {} на статус {}", userId, friendId, statusId);
        // В данном случае, обновление статуса дружбы не требуется, так как мы используем простую модель в памяти.
    }

    @Override
    public boolean deleteFriend(Long id, Long friendId) {
        log.info("Удаление дружбы между пользователями с id: {} и {}", id, friendId);
        if (!users.containsKey(id) || !users.containsKey(friendId)) {
            throw new NotFoundException("Один из пользователей не найден");
        }

        User user = users.get(id);
        User friendUser = users.get(friendId);

        Set<Long> friendSet = user.getFriends();
        friendSet.remove(friendId);
        user.setFriends(friendSet);

        friendSet = friendUser.getFriends();
        friendSet.remove(id);
        friendUser.setFriends(friendSet);

        users.put(id, user);
        users.put(friendId, friendUser);

        log.info("Дружба между пользователями с id: {} и {} удалена", id, friendId);
        return !(friendUser.getFriends().contains(id) && user.getFriends().contains(friendId));
    }

    private long getNextId() {
        return users.keySet().stream().mapToLong(Long::longValue).max().orElse(0) + 1;
    }
}
