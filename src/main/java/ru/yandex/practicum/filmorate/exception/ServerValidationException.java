package ru.yandex.practicum.filmorate.exception;

public class ServerValidationException extends ValidationException {
    public ServerValidationException(String message) {
        super(message);
    }
}
