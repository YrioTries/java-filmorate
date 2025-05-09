package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.info("Запрос на получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Запрос на получение пользователя по ID");
        return userService.getUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriendsById(@PathVariable Long id) {
        log.info("Запрос на получение списка друзей пользователя");
        return userService.getAllFriendsById(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getAllCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Запрос на получение списка общих друзей пользователей");
        return userService.getAllCommonFriendsByIds(id, otherId);
    }

    @PostMapping
    public User create(@Valid  @RequestBody User user) {
        log.info("Запрос на добавление пользователя в приложение");
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Запрос на обновление данных пользователя");
        return userService.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Запрос на добавление пользователя в список друзей");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Запрос на удаление пользователя из списка друзей");
        userService.removeFriend(id, friendId);
    }
}
