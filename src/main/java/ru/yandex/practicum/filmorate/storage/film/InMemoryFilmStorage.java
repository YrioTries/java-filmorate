package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
    }

    public LocalDate getBornOfFilms() {
        return LocalDate.of(1895, 12, 28);
    }

    public Collection<Film> getFilms() {
        return films.values();
    }

    public Collection<Long> getFilmsKeys() {
        return films.keySet();
    }

    public Film getFilm(Long id) {
        return films.get(id);
    }

    public boolean likeFilm(Long filmId, Long userId) {
        Film film = films.get(filmId);

        if (!film.getLikesFrom().contains(userId)) {
            Set<Long> likes = new TreeSet<>(film.getLikesFrom());
            likes.add(userId);
            film.setLikesFrom(likes);
            update(film);
        }
        return film.getLikesFrom().contains(userId);
    }

    public boolean unLikeFilm(Long filmId, Long userId) {
        Film film = films.get(filmId);

        if (film.getLikesFrom().contains(userId)) {
            film = films.get(filmId);
            Set<Long> likes = new TreeSet<>(film.getLikesFrom());
            likes.remove(userId);
            film.setLikesFrom(likes);
            update(film);
        }
        return !film.getLikesFrom().contains(userId);
    }

    public Film create(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    public Film update(Film newFilm) {
        if (!films.containsKey(newFilm.getId())) {
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        } else {
            Film oldFilm = films.get(newFilm.getId());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setName(newFilm.getName());
            oldFilm.setLikesFrom(newFilm.getLikesFrom());

            films.put(oldFilm.getId(), oldFilm);
            return oldFilm;
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
