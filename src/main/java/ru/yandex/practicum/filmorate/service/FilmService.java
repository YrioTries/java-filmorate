package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public Collection<Film> findAll() {
        return inMemoryFilmStorage.getFilms();
    }

    public Film get(long id) {
        return inMemoryFilmStorage.getFilm(id);
    }

    public boolean likeFilm(Long filmId, Long userId) {
        return inMemoryFilmStorage.likeFilm(filmId, userId);
    }

    public boolean unLikeFilm(Long filmId, Long userId) {
        return inMemoryFilmStorage.unLikeFilm(filmId, userId);
    }

    public Collection<Film> getPopularFilms(int count) {
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
}
