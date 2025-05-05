package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.storage.dao.user.friendstatus.FriendStatusStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class FriendStatusService {
    private final FriendStatusStorage friendStatusStorage;

    @Autowired
    public FriendStatusService(FriendStatusStorage friendStatusStorage) {
        this.friendStatusStorage = friendStatusStorage;
    }

    public Collection<FriendStatus> findAll() {
        log.info("Получение всех статусов дружбы");
        return friendStatusStorage.findAll();
    }

    public Optional<FriendStatus> get(Long id) {
        log.info("Получение статуса дружбы с id: {}", id);
        return friendStatusStorage.getFriendStatus(id);
    }
}
