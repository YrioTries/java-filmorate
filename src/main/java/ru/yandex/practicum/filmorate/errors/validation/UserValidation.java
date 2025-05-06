package ru.yandex.practicum.filmorate.errors.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public final class UserValidation {

    private UserValidation() {
        throw new UnsupportedOperationException();
    }

    public static void nullValidation(Long id, String log_msg, String error_msg) {
        if (id == null) {
            log.warn(log_msg);
            throw new ValidationException(error_msg);
        }
    }

    public static void validate(User user) {
        log.info("Запущен процесс валидации объекта User");
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Процесс валидации объекта User не пройден - Email некорректен");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Процесс валидации объекта User не пройден - логин некорректен");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if ((user.getBirthday() != null) && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Процесс валидации объекта User не пройден - дата рождения некорректна");
            throw new ValidationException("Дата рождения не может быть в будущем.");
        }
        log.info("Процесс валидации объекта User пройден успешно");
    }
}
