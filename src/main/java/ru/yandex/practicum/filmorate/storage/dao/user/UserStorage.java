package ru.yandex.practicum.filmorate.storage.dao.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    Collection<User> findAll();

    Collection<Long> findAllKeys();

    User getUser(Long id);

    Collection<Long> findAllFriends(Long id);

    Collection<Long> getCommonFriends(Long id, Long friendId);

    User create(User user);

    User update(User newUser);

    void addFriendship(Long userId, Long friendId, Long statusId);

    void removeFriendship(Long userId, Long friendId);

    Optional<Long> getFriendshipStatus(Long userId, Long friendId);

    void updateFriendshipStatus(Long userId, Long friendId, Long statusId);

    boolean deleteFriend(Long userId, Long friendId);
}
