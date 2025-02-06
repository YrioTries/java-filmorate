package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("GET запрос на получение всех пользователей");
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("POST - запрос на создание пользователя {} с id: {}", user, user.getId());
        validateUser(user);
        if (users.values().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(user.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid User newUser) {
        // Проверяем, существует ли пользователь с указанным ID
        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        } else {
            log.info("PUT - запрос на обновление пользователя {} c id: {}", newUser, newUser.getId());

            User oldUser = users.get(newUser.getId());
            validateUser(newUser);

            // Обновляем другие поля, если они указаны
            oldUser.setLogin(newUser.getLogin());

            if (oldUser.getName() == null || oldUser.getName().isBlank()) {
                oldUser.setName(oldUser.getLogin());
            } else {
                oldUser.setName(newUser.getName());
            }

            oldUser.setEmail(newUser.getEmail());
            oldUser.setBirthday(newUser.getBirthday());

            // Возвращаем обновленного пользователя
            return oldUser;
        }

    }

    private void validateUser(User user) {
        if (!user.getEmail().contains("@")) {
            throw new ConditionsNotMetException("Имейл должен быть указан и содержать символ '@'");
        }

        if (LocalDate.now().isBefore(user.getBirthday())) {
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }

    }

    // Вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
