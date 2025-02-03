package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

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

    private final LocalDate date = LocalDate.of(1895, 12, 28);
    private final LocalDateTime dateTime = date.atStartOfDay();
    private final Instant bornOfFilms = dateTime.atZone(ZoneId.systemDefault()).toInstant();

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("GET запрос на получение всех фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("POST - запрос на размещение фильма {} с id: {}", film, film.getId());
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping("/{id}")
    public Film update(@PathVariable Long id) {
        Film newFilm = films.get(id);
        log.info("PUT - запрос на обновление фильма {} с id: {}", newFilm, id);
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        validateFilm(newFilm);
        newFilm.setId(id);
        films.put(id, newFilm);
        return newFilm;
    }

    private void validateFilm(Film film) {
        if (bornOfFilms.isAfter(film.getReleaseDate())) {
            throw new ConditionsNotMetException("Дата релиза — не раньше 28 декабря 1895 года");
        }
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
