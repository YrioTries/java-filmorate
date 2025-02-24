package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
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
        return users.get(id).getFriends();
    }

    public Collection<Long> getCommonFriends(Long id, Long friendId) {
        User user = users.get(id);
        User friendUser = users.get(friendId);

        Set<Long> commonFriendSet = new TreeSet<>(user.getFriends());
        commonFriendSet.retainAll(friendUser.getFriends());

        return commonFriendSet;
    }

    public User create(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) {
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

        return oldUser;
    }

    public boolean addFriend(Long id, Long friendId) {
        User user = users.get(id);
        User friendUser = users.get(friendId);

        Set<Long> friendSet = new TreeSet<>();
        friendSet = user.getFriends();
        friendSet.add(friendId);
        user.setFriends(friendSet);
        update(user);

        friendSet = friendUser.getFriends();
        friendSet.add(id);
        friendUser.setFriends(friendSet);
        update(friendUser);

        return friendUser.getFriends().contains(id);
    }

    public boolean deleteFriend(Long id, Long friendId) {
        User user = users.get(id);
        User friendUser = users.get(friendId);

        Set<Long> friendSet = new TreeSet<>();
        friendSet = user.getFriends();
        friendSet.remove(friendId);
        user.setFriends(friendSet);
        update(user);

        friendSet = friendUser.getFriends();
        friendSet.remove(id);
        friendUser.setFriends(friendSet);
        update(friendUser);

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
