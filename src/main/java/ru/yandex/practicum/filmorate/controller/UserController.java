package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("GET запрос на получение всех пользователей");
        return userService.findAll();
    }

    @GetMapping("/users/{id}")
    public User get(@PathVariable Long id) {
        log.info("GET запрос на получение пользователя с id: {}", id);
        return userService.get(id);
    }

    @GetMapping("/users/{id}/friends")
    public Collection<Long> findAllFriends(@PathVariable Long id) {
        log.info("GET запрос на получение всех друзей пользователя {}", id);
        return userService.findAllFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public Collection<Long> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("GET запрос на получение всех общих друзей пользователей {} и {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        log.info("POST - запрос на создание пользователя {} с id: {}", user, user.getId());
        return userService.create(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("PUT - запрос на добавление пользователя {} в друзья к {}", friendId, id);
        return userService.addFriend(id, friendId);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("PUT - запрос на обновление пользователя {} c id: {}", newUser, newUser.getId());
        return userService.update(newUser);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public Long deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("DELETE - запрос на удаление пользователя {} из друзей {}",friendId, id);
        return userService.deleteFriend(id, friendId);
    }
}

