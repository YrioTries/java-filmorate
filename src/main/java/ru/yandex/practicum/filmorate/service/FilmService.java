package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.errors.validation.FilmValidation;
import ru.yandex.practicum.filmorate.errors.validation.UserValidation;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.enums.ValueOfGenre;
import ru.yandex.practicum.filmorate.model.enums.ValueOfRating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
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

    public List<Film> getAllFilms() {
        log.info("Запрос на получение списка всех фильмов");
        return List.copyOf(filmStorage.getAllFilms());
    }

    public Film getFilmById(long id) {
        log.info("Получение фильма с id: {}", id);
        FilmValidation.nullValidation(id, "[FilmService]: Id is null",
                "[FilmService]: Фильм не может быть получен если id - null");
        return filmStorage.getFilmById(id);
    }

    public void likeFilm(Long filmId, Long userId) {
        log.info("Добавление лайка фильму с id: {} от пользователя с id: {}", filmId, userId);
        FilmValidation.nullValidation(filmId, "[FilmService]: Id is null",
                "[FilmService]: Лайк к фильму не может быть добавлен если id фильма - null");
        UserValidation.nullValidation(userId, "[FilmService]: Id is null",
                "[FilmService]: Лайк к фильму не может быть добавлен если id пользователя - null");
        filmStorage.likeFilm(filmId, userId);
        log.info("Добавлен лайк фильму");
    }

    public void unLikeFilm(Long filmId, Long userId) {
        log.info("Удаление лайка с фильма с id: {} от пользователя с id: {}", filmId, userId);
        FilmValidation.nullValidation(filmId, "[FilmService]: Id is null",
                "[FilmService]: Лайк у фильма не может быть удален если id фильма - null");
        UserValidation.nullValidation(userId, "[FilmService]: Id is null",
                "[FilmService]: Лайк у фильма не может быть удален если id пользователя - null");

        filmStorage.unLikeFilm(filmId, userId);
        log.info("Удалён лайк у фильма");
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Получение {} популярных фильмов", count);
        return List.copyOf(filmStorage.getPopularFilms(count));
    }

    public Film create(Film film) {
        log.info("Создание нового фильма: {}", film);
        FilmValidation.validate(film);

        SequencedSet<Genre> validGenres = new LinkedHashSet<>();
        if ((film.getGenres() != null) && !(film.getGenres().isEmpty())) {
            for (Genre genre : film.getGenres()) {
                if ((genre.getId() < 1) || (genre.getId() > ValueOfGenre.values().length)) {
                    throw new NotFoundException("FilmDbService: Жанр с ID: " + genre.getId() + " не найден в приложении");
                }
                if ((genre.getName() != null) && (ValueOfGenre.isCorrect(genre.getName()))) {
                    validGenres.add(genre);
                } else {
                    Genre validGenre = new Genre(genre.getId(), ValueOfGenre.values()[genre.getId() - 1].getVal());
                    validGenres.add(validGenre);
                }
            }
        }

        Rating validRating;
        if (film.getMpa() == null) {
            validRating = new Rating (1, ValueOfRating.values()[0].getVal());
        } else {
            if ((film.getMpa().getId() < 1) || (film.getMpa().getId() > ValueOfRating.values().length)) {
                throw new NotFoundException("FilmDbService: MPA рейтинг с ID: " + film.getMpa().getId() + " не найден в приложении");
            }
            if ((film.getMpa().getName() != null) && (ValueOfRating.isCorrect(film.getMpa().getName()))) {
                validRating = film.getMpa();
            } else {
                validRating = new Rating (film.getMpa().getId(),
                        ValueOfRating.values()[film.getMpa().getId() - 1].getVal());
            }
        }

        Film validFilm = new Film(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                Collections.unmodifiableSequencedSet(validGenres),
                validRating
        );

        return filmStorage.create(validFilm);
    }

    public Film update(Film newFilm) {
        log.info("Обновление фильма с id: {}", newFilm.getId());
        FilmValidation.validate(newFilm);

        SequencedSet<Genre> validGenres = new LinkedHashSet<>();
        if ((newFilm.getGenres() != null) && !(newFilm.getGenres().isEmpty())) {
            for (Genre genre : newFilm.getGenres()) {
                if ((genre.getId() < 1) || (genre.getId() > ValueOfGenre.values().length)) {
                    throw new NotFoundException("FilmDbService: Жанр с ID: " + genre.getId() + " не найден в приложении");
                }
                if ((genre.getName() != null) && (ValueOfGenre.isCorrect(genre.getName()))) {
                    validGenres.add(genre);
                } else {
                    Genre validGenre = new Genre(genre.getId(), ValueOfGenre.values()[genre.getId() - 1].getVal());
                    validGenres.add(validGenre);
                }
            }
        }

        Rating validRating;
        if (newFilm.getMpa() == null) {
            validRating = new Rating(1, ValueOfRating.values()[0].getVal());
        } else {
            if ((newFilm.getMpa().getId() < 1) || (newFilm.getMpa().getId() > ValueOfRating.values().length)) {
                throw new NotFoundException("FilmDbService: MPA рейтинг с ID: " + newFilm.getMpa().getId() + " не найден в приложении");
            }
            if ((newFilm.getMpa().getName() != null) && (ValueOfRating.isCorrect(newFilm.getMpa().getName()))) {
                validRating = newFilm.getMpa();
            } else {
                validRating = new Rating(newFilm.getMpa().getId(),
                        ValueOfRating.values()[newFilm.getMpa().getId() - 1].getVal());
            }
        }

        Film validNewFilm = new Film(
                newFilm.getId(),
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                Collections.unmodifiableSequencedSet(validGenres),
                validRating
        );

        return filmStorage.update(validNewFilm);
    }

}
