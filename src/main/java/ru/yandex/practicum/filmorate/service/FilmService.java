package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.enums.GenreValueList;
import ru.yandex.practicum.filmorate.model.enums.RatingValueList;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validation.ValidationTool;

import java.util.*;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;

    private final UserStorage userStorage;

    private final static String PROGRAM_LEVEL = "FilmService";

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public List<Film> getAllFilms() {
        return List.copyOf(filmStorage.getAllFilms());
    }

    public Film getFilmById(Long id) {
        if (id == null) {
            log.warn(PROGRAM_LEVEL + ": Запрос на получение фильма по ID = null");
            throw new ValidationException(PROGRAM_LEVEL + ": Фильм не может быть получен по ID = null");
        }
        Film film = filmStorage.getFilmById(id);
        log.info(PROGRAM_LEVEL + ": Объект Film успешно найден по ID");
        return film;
    }

    public Film create(Film film) {
        ValidationTool.filmCheck(film, PROGRAM_LEVEL);

        SequencedSet<Genre> validGenresSet = new LinkedHashSet<>();
        if ((film.getGenres() != null) && !(film.getGenres().isEmpty())) {
            for (Genre genre : film.getGenres()) {
                if ((genre.getId() < 1) || (genre.getId() > GenreValueList.values().length)) {
                    throw new NotFoundException(PROGRAM_LEVEL +": Жанр с ID: " + genre.getId() + " не найден в приложении");
                }
                if ((genre.getName() != null) && (GenreValueList.isCorrectGenre(genre.getName()))) {
                    validGenresSet.add(genre);
                } else {
                    Genre validGenre = new Genre(genre.getId(), GenreValueList.values()[genre.getId() - 1].getGenre());
                    validGenresSet.add(validGenre);
                }
            }
        }

        Rating validFilmRating;
        if (film.getMpa() == null) {
            validFilmRating = new Rating(1, RatingValueList.values()[0].getRating());
        } else {
            if ((film.getMpa().getId() < 1) || (film.getMpa().getId() > RatingValueList.values().length)) {
                throw new NotFoundException("FilmDbService: MPA рейтинг с ID: " + film.getMpa().getId() + " не найден в приложении");
            }
            if ((film.getMpa().getName() != null) && (RatingValueList.isCorrectRating(film.getMpa().getName()))) {
                validFilmRating = film.getMpa();
            } else {
                validFilmRating = new Rating(film.getMpa().getId(),
                        RatingValueList.values()[film.getMpa().getId() - 1].getRating());
            }
        }

        Film validFilm = new Film(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                Collections.unmodifiableSequencedSet(validGenresSet),
                validFilmRating
        );
        return filmStorage.create(validFilm);
    }

    public Film update(Film film) {
        ValidationTool.filmCheck(film, PROGRAM_LEVEL);

        getFilmById(film.getId());

        SequencedSet<Genre> validGenresSet = new LinkedHashSet<>();
        if ((film.getGenres() != null) && !(film.getGenres().isEmpty())) {
            for (Genre genre : film.getGenres()) {
                if ((genre.getId() < 1) || (genre.getId() > GenreValueList.values().length)) {
                    throw new NotFoundException("FilmDbService: Жанр с ID: " + genre.getId() + " не найден в приложении");
                }
                if ((genre.getName() != null) && (GenreValueList.isCorrectGenre(genre.getName()))) {
                    validGenresSet.add(genre);
                } else {
                    Genre validGenre = new Genre(genre.getId(), GenreValueList.values()[genre.getId() - 1].getGenre());
                    validGenresSet.add(validGenre);
                }
            }
        }

        Rating validFilmRating;
        if (film.getMpa() == null) {
            validFilmRating = new Rating(1, RatingValueList.values()[0].getRating());
        } else {
            if ((film.getMpa().getId() < 1) || (film.getMpa().getId() > RatingValueList.values().length)) {
                throw new NotFoundException("FilmDbService: MPA рейтинг с ID: " + film.getMpa().getId() + " не найден в приложении");
            }
            if ((film.getMpa().getName() != null) && (RatingValueList.isCorrectRating(film.getMpa().getName()))) {
                validFilmRating = film.getMpa();
            } else {
                validFilmRating = new Rating(film.getMpa().getId(),
                        RatingValueList.values()[film.getMpa().getId() - 1].getRating());
            }
        }

        Film validFilm = new Film(
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                Collections.unmodifiableSequencedSet(validGenresSet),
                validFilmRating
        );
        return filmStorage.update(validFilm);
    }


    public void addLike(Long filmId, Long userId) {
        ValidationTool.checkForNull(filmId, PROGRAM_LEVEL, "Лайк к фильму не может быть добален по ID фильма = null");

        ValidationTool.checkForNull(userId, PROGRAM_LEVEL, "Лайк к фильму не может быть добален по ID пользователя = null");

        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);

        filmStorage.addLike(filmId, userId);
        log.info("Лайк фильму успешно добавлен");
    }

    public void removeLike(Long filmId, Long userId) {
        ValidationTool.checkForNull(filmId, PROGRAM_LEVEL, "Лайк у фильма не может быть удален по ID фильма = null");

        ValidationTool.checkForNull(userId, PROGRAM_LEVEL, "Лайк у фильма не может быть удален по ID пользователя = null");

        filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);

        filmStorage.removeLike(filmId, userId);
        log.info("Лайк фильма успешно удален");
    }

    public List<Film> getPopularFilms(int limit) {
        return List.copyOf(filmStorage.getTopFilms(limit));
    }
}
