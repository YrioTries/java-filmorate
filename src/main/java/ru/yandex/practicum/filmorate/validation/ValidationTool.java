package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

/**
 * Вспомогательный класс со статическими методами проверки объектов Film
 * в соотвествии с заданными в ТЗ критериями.
 */
@Slf4j
public final class ValidationTool {
    private static final int DESCRIPTION_SIZE = 200;
    private static final LocalDate RELEASE_FIRST_FILM_DATE = LocalDate.of(1895, 12, 28);

    private ValidationTool() {
        throw new UnsupportedOperationException();
    }

    public static void checkForNull(Long value, String level, String description) {
        if (value == null) {
            throw new ValidationException("[" + level + "]: " + description);
        }
    }

    public static void checkForNull(Long value1, Long value2, String level, String description) {
        if (value1 == null || value2 == null) {
            throw new ValidationException("[" + level + "]: " + description);
        }
    }

    public static void checkId(Long id, String level, String description) {
        if (id == null  || id < 1L) {
            throw new ValidationException("[" + level + "]: " + description);
        }
    }

    public static void checkId(Long id1, Long id2, String level, String description) {
        if (id1 == null  || id2 == null || id1 < 1L || id2 < 1L) {
            throw new ValidationException("[" + level + "]: " + description);
        }
    }

    public static void filmCheck(Film film, String level) {
        log.info("Запущен процесс валидации объекта Film");
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Процесс валидации объекта Film не пройден - название некорректно");
            throw new ValidationException("[" + level + "]:  Название фильма не может быть пустым");
        }
        if ((film.getDescription() != null) && (film.getDescription().length() > DESCRIPTION_SIZE)) {
            log.warn("Процесс валидации объекта Film не пройден - описание некорректно");
            throw new ValidationException("[" + level + "]:  Превышена максимально допустимая длина описания ");
        }
        if ((film.getReleaseDate() != null) && (film.getReleaseDate().isBefore(RELEASE_FIRST_FILM_DATE))) {
            log.warn("Процесс валидации объекта Film не пройден - дата релиза некорректна");
            throw new ValidationException("[" + level + "]: Дата релиза фильма указана ранее эталонной даты 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            log.warn("Процесс валидации объекта Film не пройден - продолжительность некорректна");
            throw new ValidationException("[" + level + "]: Продолжительность фильма должна быть положительным числом");
        }
        log.info("Процесс валидации объекта Film пройден успешно");
    }

    public static void userCheck(User user, String level) {
        log.info("Запущен процесс валидации объекта User");
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Процесс валидации объекта User не пройден - Email некорректен");
            throw new ValidationException("[" + level + "]: Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Процесс валидации объекта User не пройден - логин некорректен");
            throw new ValidationException("[" + level + "]: Логин не может быть пустым и содержать пробелы");
        }
        if ((user.getBirthday() != null) && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Процесс валидации объекта User не пройден - дата рождения некорректна");
            throw new ValidationException("[" + level + "]: Дата рождения не может быть в будущем.");
        }
        log.info("Процесс валидации объекта User пройден успешно");
    }
}
