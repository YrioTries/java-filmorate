package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {

    List<User> getAllUsers();

    User getUserById(Long userId);

    List<User> getAllFriendsById(Long userId);

    List<User> getUsersByIdSet(Set<Long> ids);

    Set<Long> getUserFriendsIdsById(Long userId);

    User create(User user);

    User update(User user);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

}
