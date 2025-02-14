package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final InMemoryFilmStorage inMemoryFilmStorage;

    private final Map<Long, Film> films = new HashMap<>();

    private final LocalDate bornOfFilms = LocalDate.of(1895, 12, 28);

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film create(Film film) {

        validateFilm(film);

        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    public Collection<Long> userLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        Set<Long>likes = new TreeSet<>(film.getLikesFrom());
        likes.add(userId);
        film.setLikesFrom(likes);
        update(film);

        return film.getLikesFrom();
    }

    public boolean unLike(Long filmId, Long userId) {
        Film film = films.get(filmId);
        Set<Long>likes = new TreeSet<>(film.getLikesFrom());
        likes.remove(userId);
        film.setLikesFrom(likes);
        update(film);

        return true;
    }

    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted(Comparator.comparingLong((Film film) -> film.getLikesFrom().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }


    public Film update(Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        } else {
            Film oldFilm = films.get(newFilm.getId());
            validateFilm(oldFilm);
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setName(newFilm.getName());

            films.put(oldFilm.getId(), oldFilm);
            return oldFilm;
        }
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("Некорректное название фильма");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Некорректное описание фильма");
        }
        if (film.getReleaseDate() == null || bornOfFilms.isAfter(film.getReleaseDate())) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Длительность должна быть больше нуля");
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
