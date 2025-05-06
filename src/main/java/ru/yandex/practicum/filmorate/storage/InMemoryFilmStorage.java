package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
@Qualifier("InMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films;

    public InMemoryFilmStorage() {
        films = new HashMap<>();
    }

    @Override
    public Collection<Film> getFilms() {
        log.info("Получение всех фильмов из памяти");
        return films.values();
    }

    @Override
    public Collection<Long> getFilmsKeys() {
        log.info("Получение всех ключей фильмов из памяти");
        return films.keySet();
    }

    @Override
    public Film getFilm(Long id) {
        log.info("Получение фильма с id: {} из памяти", id);
        return films.get(id);
    }

    @Override
    public void likeFilm(Long filmId, Long userId) {
        log.info("Добавление лайка фильму с id: {} от пользователя с id: {}", filmId, userId);
        Film film = films.get(filmId);

        if (!film.getLikesFrom().contains(userId)) {
            Set<Long> likes = new TreeSet<>(film.getLikesFrom());
            likes.add(userId);
            film.setLikesFrom(likes);
            update(film);
        }
        return film.getLikesFrom().contains(userId);
    }

    @Override
    public boolean unLikeFilm(Long filmId, Long userId) {
        log.info("Удаление лайка с фильма с id: {} от пользователя с id: {}", filmId, userId);
        Film film = films.get(filmId);

        if (film.getLikesFrom().contains(userId)) {
            Set<Long> likes = new TreeSet<>(film.getLikesFrom());
            likes.remove(userId);
            film.setLikesFrom(likes);
            update(film);
        }
        return !film.getLikesFrom().contains(userId);
    }

    @Override
    public Film create(Film film) {
        log.info("Создание нового фильма: {}", film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Создан новый фильм с id: {}", film.getId());
        return film;
    }

    @Override
    public Film update(Film newFilm) {
        log.info("Обновление фильма с id: {}", newFilm.getId());
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
            log.info("Фильм с id: {} обновлен", newFilm.getId());
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
