package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.*;

@Slf4j
@Service
public class RatingService {

    private final RatingStorage mpaRatingStorage;

    private final static String PROGRAM_LEVEL = "RatingService";

    @Autowired
    public RatingService(RatingStorage mpaRatingStorage) {
        this.mpaRatingStorage = mpaRatingStorage;
    }

    public List<Rating> getAllRatings() {
        return List.copyOf(mpaRatingStorage.getAllRatings());
    }

    public Rating getRatingById(Integer id) {
        //ValidationTool.checkForNull(id, PROGRAM_LEVEL, "Рейтинг не может быть получен по ID = null");
        if (id == null) {
            throw new ValidationException("Рейтинг не может быть получен по ID = null");
        }
        return mpaRatingStorage.getRatingById(id);
    }
}
