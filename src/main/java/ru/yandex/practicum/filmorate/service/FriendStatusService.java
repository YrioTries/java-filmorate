package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.storage.dao.user.friendstatus.FriendStatusStorage;

import java.util.Collection;
import java.util.Optional;

@Service
public class FriendStatusService {
    private final FriendStatusStorage friendStatusStorage;

    @Autowired
    public FriendStatusService(FriendStatusStorage friendStatusStorage) {
        this.friendStatusStorage = friendStatusStorage;
    }

    public Collection<FriendStatus> findAll() {
        return friendStatusStorage.findAll();
    }

    public Optional<FriendStatus> get(Long id) {
        return friendStatusStorage.getFriendStatus(id);
    }
}

