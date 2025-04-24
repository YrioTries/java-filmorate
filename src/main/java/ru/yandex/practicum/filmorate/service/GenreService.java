package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.enums.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.dao.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.film.dao.genre.GenreStorage;

import java.util.Collection;
import java.util.Optional;

@Service
public class GenreService {

    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreDbStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Collection<Genre> findAll() {
        return genreStorage.findAll();
    }

    public Optional<Genre> get(Long id) {
        return genreStorage.getGenre(id);
    }
}
