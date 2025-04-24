package ru.yandex.practicum.filmorate.storage.film.dao.genre;

import ru.yandex.practicum.filmorate.enums.film.Genre;

import java.util.Collection;
import java.util.Optional;

public interface GenreStorage {

    Collection<Genre> findAll();

    Optional<Genre> getGenre(Long id);
}

