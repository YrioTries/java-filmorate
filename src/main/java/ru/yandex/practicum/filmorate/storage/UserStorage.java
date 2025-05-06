package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Set;

public interface UserStorage {

    List<User> getAllUsers();

    User getUserById(Long id);

    List<User> getFriendsById(Long id);

    Set<Long> getUserFriendsIdsById(Long userId);

    List<User> getUsersByIdSet(Set<Long> ids);

    User create(User user);

    User update(User newUser);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);
}
