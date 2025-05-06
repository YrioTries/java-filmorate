package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class RatingController {
    private final RatingService RatingService;

    @Autowired
    public RatingController(RatingService RatingService) {
        this.RatingService = RatingService;
    }

    @GetMapping
    public List<Rating> getAllMpaRatings() {
        log.info("Запрос на получение списка всех рейтингов");
        return RatingService.getAllMpaRatings();
    }

    @GetMapping("/{id}")
    public Rating getMpaRatingById(@PathVariable Integer id) {
        log.info("Запрос на получение рейтинга по ID");
        return RatingService.getMpaRatingById(id);
    }
}
