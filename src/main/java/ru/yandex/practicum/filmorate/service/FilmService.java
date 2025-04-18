package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final InMemoryFilmStorage inMemoryFilmStorage;

    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Collection<Film> findAll() {
        if (inMemoryFilmStorage.getFilms().isEmpty()) {
            throw new NotFoundException("Нет созданных фильмов");
        }
        return inMemoryFilmStorage.getFilms();
    }

    public Film get(long id) {
        filmExist(id);
        return inMemoryFilmStorage.getFilm(id);
    }

    public boolean likeFilm(Long filmId, Long userId) {
        filmExist(filmId);
        errorOfUserExist(userId);
        return inMemoryFilmStorage.likeFilm(filmId, userId);
    }

    public boolean unLikeFilm(Long filmId, Long userId) {
        filmExist(filmId);
        errorOfUserExist(userId);

        if (inMemoryFilmStorage.unLikeFilm(filmId, userId)) {
            return true;
        } else {
            throw new NotFoundException("Не получилось удалить друга");
        }
    }

    public Collection<Film> getPopularFilms(int count) {
        if (inMemoryFilmStorage.getFilms().isEmpty()) {
            throw new NotFoundException("Нет созданных фильмов");
        }
        return inMemoryFilmStorage.getFilms()
                .stream()
                .sorted(Comparator.comparingLong((Film film) -> film.getLikesFrom().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film create(Film film) {
        validateFilm(film);
        return inMemoryFilmStorage.create(film);
    }

    public Film update(Film newFilm) {
        filmExist(newFilm.getId());
        validateFilm(newFilm);
        return inMemoryFilmStorage.update(newFilm);
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("Некорректное название фильма");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Некорректное описание фильма");
        }
        if (film.getReleaseDate() == null || inMemoryFilmStorage.getBornOfFilms().isAfter(film.getReleaseDate())) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Длительность должна быть больше нуля");
        }
    }

    private void filmExist(Long id) {
        if (!inMemoryFilmStorage.getFilmsKeys().contains(id)) {
            throw new NotFoundException("Фильм не найден");
        }
    }

    private void errorOfUserExist(Long id) {
        if (inMemoryUserStorage.findAllKeys() != null && !inMemoryUserStorage.findAllKeys().contains(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        } else if (inMemoryUserStorage.findAllKeys() == null) {
            throw new NotFoundException("Нет активных пользователей");
        }
    }
}
