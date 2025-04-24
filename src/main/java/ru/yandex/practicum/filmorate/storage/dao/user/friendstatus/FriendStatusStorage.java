package ru.yandex.practicum.filmorate.storage.dao.user.friendstatus;

import ru.yandex.practicum.filmorate.model.FriendStatus;

import java.util.Collection;
import java.util.Optional;

public interface FriendStatusStorage {
    Collection<FriendStatus> findAll();
    Optional<FriendStatus> getFriendStatus(Long id);
}
