package ru.yandex.practicum.filmorate.storage.dao.film.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {
    Collection<Genre> findAll();
    Optional<Genre> getGenre(Long id);
}

