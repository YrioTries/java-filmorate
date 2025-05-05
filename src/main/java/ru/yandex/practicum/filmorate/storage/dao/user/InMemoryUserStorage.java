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
        return users.values();
    }

    @Override
    public Collection<Long> findAllKeys() {
        return users.keySet();
    }

    @Override
    public User getUser(Long id) {
        return users.get(id);
    }

    @Override
    public Collection<Long> findAllFriends(Long id) {
        return users.getOrDefault(id, new User()).getFriends();
    }

    @Override
    public Set<Long> getCommonFriends(Long id, Long friendId) {
        Set<Long> friendsOfUser1 = new HashSet<>(users.getOrDefault(id, new User()).getFriends());
        Set<Long> friendsOfUser2 = new HashSet<>(users.getOrDefault(friendId, new User()).getFriends());
        friendsOfUser1.retainAll(friendsOfUser2);
        return friendsOfUser1;
    }

    @Override
    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        User oldUser = users.get(newUser.getId());
        if (oldUser == null) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        oldUser.setLogin(newUser.getLogin());
        oldUser.setName(newUser.getName() != null && !newUser.getName().isBlank() ? newUser.getName() : newUser.getLogin());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setBirthday(newUser.getBirthday());

        users.put(oldUser.getId(), oldUser);
        return oldUser;
    }

    @Override
    public void addFriendship(Long userId, Long friendId, Long statusId) {
        User user = users.get(userId);
        User friendUser = users.get(friendId);

        if (user != null && friendUser != null) {
            user.getFriends().add(friendId);
            friendUser.getFriends().add(userId);

            users.put(userId, user);
            users.put(friendId, friendUser);
        }
    }

    @Override
    public void removeFriendship(Long userId, Long friendId) {
        User user = users.get(userId);
        User friendUser = users.get(friendId);

        if (user != null && friendUser != null) {
            user.getFriends().remove(friendId);
            friendUser.getFriends().remove(userId);

            users.put(userId, user);
            users.put(friendId, friendUser);
        }
    }

    @Override
    public Optional<Long> getFriendshipStatus(Long userId, Long friendId) {
        User user = users.get(userId);
        if (user != null && user.getFriends().contains(friendId)) {
            return Optional.of(1L); // Предположим, что статус дружбы всегда 1 (запрос на дружбу)
        }
        return Optional.empty();
    }

    @Override
    public void updateFriendshipStatus(Long userId, Long friendId, Long statusId) {
        // В данном случае, обновление статуса дружбы не требуется, так как мы используем простую модель в памяти.
    }

    public boolean deleteFriend(Long id, Long friendId) {
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

        return !(friendUser.getFriends().contains(id) && user.getFriends().contains(friendId));
    }

    private long getNextId() {
        return users.keySet().stream().mapToLong(Long::longValue).max().orElse(0) + 1;
    }
}
