package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class RatingService {
    private final RatingStorage mpaRatingStorage;

    @Autowired
    public RatingService(RatingStorage mpaRatingStorage) {
        this.mpaRatingStorage = mpaRatingStorage;
    }

    public List<Rating> getAllMpaRatings() {
        return List.copyOf(mpaRatingStorage.getAllRatings());
    }

    public Rating getMpaRatingById(Integer id) {
        if (id == null) {
            log.warn("Запрос на получение рейтинга по ID = null");
            throw new ValidationException("Рейтинг не может быть получен по ID = null");
        }
        return mpaRatingStorage.getRatingById(id);
    }
}
