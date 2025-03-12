package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> findAll();

    Collection<Long> findAllKeys();

    User getUser(Long id);

    Collection<Long> findAllFriends(Long id);

    Collection<Long> getCommonFriends(Long id, Long friendId);

    User create(User user);

    User update(User newUser);

    long addFriend(Long id, Long friendId);

    boolean deleteFriend(Long id, Long friendId);
}
