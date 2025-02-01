package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("GET запрос на получение всех фильмов");
        return films.values();
    }

    @PostMapping
    public User create(@RequestBody Film film) {
        log.info("POST - запрос на размещение фильма {} с id: {}", film, film.getId());
        // Проверяем, указан ли адрес электронной почты
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        // Проверяем, используется ли указанный адрес электронной почты
        boolean isDuplicated = films.values()
                .stream()
                .anyMatch(u -> u.getEmail().equalsIgnoreCase(film.getEmail()));

        if (isDuplicated) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        // Формируем дополнительные данные
        film.setId(getNextId());
        // user.setBirthday(Instant.now());
        // Сохраняем нового пользователя в памяти приложения
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        log.info("PUT - запрос на обновление пользователя {} c id: {}", newUser, newUser.getId());
        // Проверяем, указан ли ID
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        // Проверяем, существует ли пользователь с указанным ID
        if (!users.containsKey(newUser.getId())) {
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        User oldUser = users.get(newUser.getId());

        // Проверяем, изменяется ли адрес электронной почты
        if (newUser.getEmail() != null && !newUser.getEmail().equalsIgnoreCase(oldUser.getEmail())) {
            boolean isDuplicated = users.values()
                    .stream()
                    .anyMatch(u -> u.getEmail().equalsIgnoreCase(newUser.getEmail()));

            if (isDuplicated) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            oldUser.setEmail(newUser.getEmail());
        }

        // Обновляем другие поля, если они указаны
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }

        if (newUser.getLogin() != null) {
            oldUser.setLogin(newUser.getLogin());
        }

        // Возвращаем обновленного пользователя
        return oldUser;
    }

    // Вспомогательный метод для генерации идентификатора нового пользователя
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
