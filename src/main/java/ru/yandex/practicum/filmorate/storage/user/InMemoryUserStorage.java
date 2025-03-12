package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
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

        User user = getUser(id);
        User friendUser = getUser(friendId);

        log.info("Пользователи user {} и friendUser {} делятся списком общих друзей", user.getId(), friendUser.getId());

        Set<Long> friendsOfUser1 = new HashSet<>(user.getFriends());
        Set<Long> friendsOfUser2 = new HashSet<>(friendUser.getFriends());
        friendsOfUser1.retainAll(friendsOfUser2);

        return new HashSet<>(friendsOfUser1);
    }

    public User create(User user) {
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

        if (newUser.getName() == null || newUser.getName().isBlank()) {
            oldUser.setName(newUser.getLogin());
        } else {
            oldUser.setName(newUser.getName());
        }

        oldUser.setEmail(newUser.getEmail());
        oldUser.setBirthday(newUser.getBirthday());

        users.put(oldUser.getId(), oldUser);
        return oldUser;
    }

    public long addFriend(Long id, Long friendId) {
        if (!users.containsKey(id) || !users.containsKey(friendId)) {
            throw new NotFoundException("Один из пользователей не найден");
        }

        User user = getUser(id);
        User friendUser = getUser(friendId);

        if (!(user.getFriends().contains(friendId) && friendUser.getFriends().contains(id))) {
            log.info("Пользователи user {} и friendUser {} становятся друзьями", user.getId(), friendUser.getId());

            Set<Long> friendSet = user.getFriends();
            friendSet.add(friendId);
            user.setFriends(friendSet);

            friendSet = friendUser.getFriends();
            friendSet.add(id);
            friendUser.setFriends(friendSet);

            users.put(user.getId(), user);
            users.put(friendUser.getId(), friendUser);
        }

        if (user.getFriends().contains(friendId) && friendUser.getFriends().contains(id)){
            return friendId;
        } else {
            throw new NotFoundException("Ошибка добавления в друзья");
        }
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
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
