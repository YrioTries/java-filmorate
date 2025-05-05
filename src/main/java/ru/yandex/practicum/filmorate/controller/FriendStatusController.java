package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.FriendStatusService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/friend-statuses")
public class FriendStatusController {
    private final FriendStatusService friendStatusService;

    @Autowired
    public FriendStatusController(FriendStatusService friendStatusService) {
        this.friendStatusService = friendStatusService;
    }

    @GetMapping
    public Collection<FriendStatus> findAll() {
        log.info("GET запрос на получение всех статусов дружбы");
        return friendStatusService.findAll();
    }

    @GetMapping("/{id}")
    public FriendStatus get(@PathVariable Long id) {
        log.info("GET запрос на получение статуса дружбы с id: {}", id);
        return friendStatusService.get(id).orElseThrow(() -> new NotFoundException("Статус дружбы не найден"));
    }
}
