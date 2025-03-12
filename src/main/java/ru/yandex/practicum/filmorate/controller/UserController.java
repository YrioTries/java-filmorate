package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.IdValue; // Импортируем IdValue
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        log.info("GET запрос на получение пользователя с id: {}", id);
        return userService.get(id);
    }

    @GetMapping("/{id}/friends")
    public List<IdValue> findAllFriends(@PathVariable Long id) {
        log.info("GET запрос на получение всех друзей пользователя {}", id);
        Collection<Long> friendIds = userService.findAllFriends(id);
        return friendIds.stream()
                .map(IdValue::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<IdValue> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("GET запрос на получение всех общих друзей пользователей {} и {}", id, otherId);
        Collection<Long> commonFriendIds = userService.getCommonFriends(id, otherId); // Получаем Collection<Long>
        return commonFriendIds.stream()
                .map(IdValue::new)
                .collect(Collectors.toList());
    }

    @PostMapping()
    public User create(@Valid @RequestBody User user) {
        log.info("POST - запрос на создание пользователя {}", user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("PUT - запрос на обновление пользователя {} c id: {}", newUser, newUser.getId());
        return userService.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<IdValue> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("PUT - запрос на добавление пользователя {} в друзья к {}", friendId, id);
        userService.addFriend(id, friendId); // Вызываем service
        return ResponseEntity.ok(new IdValue(friendId)); // Возвращаем ResponseEntity
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public boolean deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("DELETE - запрос на удаление пользователя {} из друзей {}",friendId, id);
        return userService.deleteFriend(id, friendId);
    }
}
