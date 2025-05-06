package ru.yandex.practicum.filmorate.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse MethodArgumentTypeMismatchError(final MethodArgumentTypeMismatchException e) {
        return new ErrorResponse("ERROR[400]: Произошла ошибка MethodArgumentTypeMismatchException: ", e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse ValidationExceptionError(final ValidationException e) {
        return new ErrorResponse("ERROR[400]: Произошла ошибка ValidationException: ", e.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse HttpMediaTypeNotSupportedError(final HttpMediaTypeNotSupportedException e) {
        return new ErrorResponse("ERROR[400]: Произошла ошибка HttpMediaTypeNotSupportedException: ", e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse HttpMessageNotReadableError(final HttpMessageNotReadableException e) {
        return new ErrorResponse("ERROR[400]: Произошла ошибка HttpMessageNotReadableException: ", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse NotFoundExceptionError(final NotFoundException e) {
        return new ErrorResponse("ERROR[404]: Произошла ошибка NotFoundException: ", e.getMessage());
    }

    @ExceptionHandler(InternalServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse InternalServerError(final InternalServerException e) {
        return new ErrorResponse("ERROR[500]: Произошла ошибка InternalServerException: ", e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse ThrowableError(final Throwable e) {
        return new ErrorResponse("ERROR[500]: Произошла ошибка Throwable: ", e.getMessage());
    }
}


