package ru.yandex.practicum.filmorate.storage.dao.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> getFilms();

    Collection<Long> getFilmsKeys();

    Film getFilm(Long id);

    boolean likeFilm(Long filmId, Long userId);

    boolean unLikeFilm(Long filmId, Long userId);

    Film create(Film film);

    Film update(Film newFilm);
}
