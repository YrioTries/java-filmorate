package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dao.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    @Qualifier("SQL_Film_Storage")
    private final FilmStorage filmStorage;

    @Qualifier("SQL_User_Storage")
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("SQL_Film_Storage") FilmStorage filmStorage, @Qualifier("SQL_User_Storage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> findAll() {
        log.info("Получение всех фильмов");
        if (filmStorage.getFilms().isEmpty()) {
            throw new NotFoundException("Нет созданных фильмов");
        }
        return filmStorage.getFilms();
    }

    public Film get(long id) {
        log.info("Получение фильма с id: {}", id);
        filmExist(id);
        return filmStorage.getFilm(id);
    }

    public boolean likeFilm(Long filmId, Long userId) {
        log.info("Добавление лайка фильму с id: {} от пользователя с id: {}", filmId, userId);
        filmExist(filmId);
        errorOfUserExist(userId);
        return filmStorage.likeFilm(filmId, userId);
    }

    public boolean unLikeFilm(Long filmId, Long userId) {
        log.info("Удаление лайка с фильма с id: {} от пользователя с id: {}", filmId, userId);
        filmExist(filmId);
        errorOfUserExist(userId);

        if (filmStorage.unLikeFilm(filmId, userId)) {
            return true;
        } else {
            throw new NotFoundException("Не получилось удалить лайк");
        }
    }

    public Collection<Film> getPopularFilms(int count) {
        log.info("Получение {} популярных фильмов", count);
        if (filmStorage.getFilms().isEmpty()) {
            throw new NotFoundException("Нет созданных фильмов");
        }
        return filmStorage.getFilms()
                .stream()
                .sorted(Comparator.comparingLong((Film film) -> film.getLikesFrom().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film create(Film film) {
        log.info("Создание нового фильма: {}", film);
        validateFilm(film);
        return filmStorage.create(film);
    }

    public Film update(Film newFilm) {
        log.info("Обновление фильма с id: {}", newFilm.getId());
        filmExist(newFilm.getId());
        validateFilm(newFilm);
        return filmStorage.update(newFilm);
    }

    private void validateFilm(Film film) {
        log.info("Валидация фильма: {}", film);
        if (film.getName() == null || film.getName().isEmpty()) {
            throw new ValidationException("Некорректное название фильма");
        }
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ValidationException("Некорректное описание фильма");
        }
        if (film.getReleaseDate() == null || getBornOfFilms().isAfter(film.getReleaseDate())) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Длительность должна быть больше нуля");
        }
    }

    private void filmExist(Long id) {
        log.info("Проверка существования фильма с id: {}", id);
        if (!filmStorage.getFilmsKeys().contains(id)) {
            throw new NotFoundException("Фильм не найден");
        }
    }

    private void errorOfUserExist(Long id) {
        log.info("Проверка существования пользователя с id: {}", id);
        if (userStorage.findAllKeys() != null && !userStorage.findAllKeys().contains(id)) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        } else if (userStorage.findAllKeys() == null) {
            throw new NotFoundException("Нет активных пользователей");
        }
    }

    public LocalDate getBornOfFilms() {
        return LocalDate.of(1895, 12, 28);
    }
}
