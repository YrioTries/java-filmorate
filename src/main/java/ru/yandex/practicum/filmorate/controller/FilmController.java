package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class FilmController {

    LocalDate date = LocalDate.of(1895, 12, 28);
    LocalDateTime dateTime = date.atStartOfDay();
    Instant bornOfFilms = dateTime.atZone(ZoneId.systemDefault()).toInstant();

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("GET запрос на получение всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("POST - запрос на размещение фильма {} с id: {}", film, film.getId());

        if (film.getName() == null || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Название не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            throw new ConditionsNotMetException("Максимальная длина описания — 200 символов");
        }


        if (bornOfFilms.isAfter(film.getReleaseDate())) {
            throw new ConditionsNotMetException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        // Формируем дополнительные данные
        film.setId(getNextId());
        Instant s;
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public User update(@Valid @RequestBody Film newFilm) {
        log.info("PUT - запрос на обновление фильма {} c id: {}", newFilm, newFilm.getId());
        // Проверяем, указан ли ID
        if (newFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        // Проверяем, существует ли пользователь с указанным ID
        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Пользователь с id = " + newFilm.getId() + " не найден");
        }

        Film oldFilm = films.get(newFilm.getId());

        // Проверяем, изменяется ли адрес электронной почты
        if (newFilm.getEmail() != null && !newFilm.getEmail().equalsIgnoreCase(oldFilm.getEmail())) {
            boolean isDuplicated = films.values()
                    .stream()
                    .anyMatch(u -> u.getEmail().equalsIgnoreCase(newFilm.getEmail()));

            if (isDuplicated) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
            oldFilm.setEmail(newFilm.getEmail());
        }

        // Обновляем другие поля, если они указаны
        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }

        if (newFilm.getLogin() != null) {
            oldFilm.setLogin(newFilm.getLogin());
        }

        // Возвращаем обновленного пользователя
        return oldFilm;
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
