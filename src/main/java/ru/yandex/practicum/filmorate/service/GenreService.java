package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<Genre> getAllGenres() {
        return List.copyOf(genreStorage.getAllGenres());
    }

    public Genre getGenreById(Integer id) {
        if (id == null) {
            log.warn("Запрос на получение жанра по ID = null");
            throw new ValidationException("Жанр не может быть получен по ID = null");
        }
        return genreStorage.getGenreById(id);
    }
}
