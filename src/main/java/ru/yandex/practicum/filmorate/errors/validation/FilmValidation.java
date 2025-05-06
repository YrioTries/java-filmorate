package ru.yandex.practicum.filmorate.errors.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
public final class FilmValidation {
    private static final int LENGTH_OF_DESCRIPTION = 200;
    private static final LocalDate FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    private FilmValidation() {
        throw new UnsupportedOperationException();
    }

    public static void nullValidation(Long id, String log_msg, String error_msg) {
        if (id == null) {
            log.warn(log_msg);
            throw new ValidationException(error_msg);
        }
    }

    public static void validate(Film film) {
        log.info("Запущен процесс валидации объекта Film");
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Процесс валидации объекта Film не пройден - название некорректно");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if ((film.getDescription() != null) && (film.getDescription().length() > LENGTH_OF_DESCRIPTION)) {
            log.warn("Процесс валидации объекта Film не пройден - описание некорректно");
            throw new ValidationException("Превышена максимально допустимая длина описания ");
        }
        if ((film.getReleaseDate() != null) && (film.getReleaseDate().isBefore(FIRST_FILM_DATE))) {
            log.warn("Процесс валидации объекта Film не пройден - дата релиза некорректна");
            throw new ValidationException("Дата релиза фильма указана ранее эталонной даты 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            log.warn("Процесс валидации объекта Film не пройден - продолжительность некорректна");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        log.info("Процесс валидации объекта Film пройден успешно");
    }
}
