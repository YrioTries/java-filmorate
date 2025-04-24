package ru.yandex.practicum.filmorate.storage.dao.film.rating;

import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

public interface RatingStorage {
    Collection<Rating> findAll();
    Optional<Rating> getRating(Long id);
}
