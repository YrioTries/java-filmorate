package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Collection<User> getAllUsers();

    User getUserById(Long id);

    Collection<Long> getAllFriends(Long id);

    List<User> getFriendsById(Long userId);

    Collection<Long> getCommonFriends(Long id, Long friendId);

    User create(User user);

    User update(User newUser);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);
}
