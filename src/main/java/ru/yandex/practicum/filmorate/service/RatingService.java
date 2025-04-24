package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.dao.film.rating.RatingStorage;

import java.util.Collection;
import java.util.Optional;

@Service
public class RatingService {
    private final RatingStorage ratingStorage;

    @Autowired
    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public Collection<Rating> findAll() {
        return ratingStorage.findAll();
    }

    public Optional<Rating> get(Long id) {
        return ratingStorage.getRating(id);
    }
}
