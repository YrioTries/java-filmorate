package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.dao.film.rating.RatingStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class RatingService {
    private final RatingStorage ratingStorage;

    @Autowired
    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public Collection<Rating> findAll() {
        log.info("Получение всех рейтингов");
        return ratingStorage.findAll();
    }

    public Optional<Rating> get(Long id) {
        log.info("Получение рейтинга с id: {}", id);
        return ratingStorage.getRating(id);
    }
}
