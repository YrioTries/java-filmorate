package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users;

    public InMemoryUserStorage() {
        users = new HashMap<>();
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public Collection<Long> findAllKeys() {
        return users.keySet();
    }

    public User getUser(Long id) {
        return users.get(id);
    }

    public Collection<Long> findAllFriends(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return users.get(id).getFriends();
    }

    public Set<Long> getCommonFriends(Long id, Long friendId) {
        if (!users.containsKey(id) || !users.containsKey(friendId)) {
            throw new NotFoundException("Один из пользователей не найден");
        }
        User user = users.get(id);
        User friendUser = users.get(friendId);

        if (user.getFriends() == null || friendUser.getFriends() == null) {
            return Collections.emptySet(); // Возвращаем пустое множество, если один из списков друзей null
        }

        Set<Long> commonFriendSet = new HashSet<>(user.getFriends());
        commonFriendSet.retainAll(friendUser.getFriends());

        return commonFriendSet;
    }


    public User create(User user) {
        if (users.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }


    public User update(User newUser) {
        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }
        User oldUser = users.get(newUser.getId());

        oldUser.setLogin(newUser.getLogin());

        if (oldUser.getName() == null || oldUser.getName().isBlank()) {
            oldUser.setName(oldUser.getLogin());
        } else {
            oldUser.setName(newUser.getName());
        }

        oldUser.setEmail(newUser.getEmail());
        oldUser.setBirthday(newUser.getBirthday());
        oldUser.setFriends(newUser.getFriends());

        users.put(newUser.getId(), oldUser);
        return oldUser;
    }

    public boolean addFriend(Long id, Long friendId) {
        if (!users.containsKey(id) || !users.containsKey(friendId)) {
            throw new NotFoundException("Один из пользователей не найден");
        }
        User user = users.get(id);
        User friendUser = users.get(friendId);

        Set<Long> friendSet = user.getFriends();
        friendSet.add(friendId);
        user.setFriends(friendSet);
        users.put(id, user);

        friendSet = friendUser.getFriends();
        friendSet.add(id);
        friendUser.setFriends(friendSet);
        users.put(friendId, friendUser);

        return friendUser.getFriends().contains(id);
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

        return !friendUser.getFriends().contains(id);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
