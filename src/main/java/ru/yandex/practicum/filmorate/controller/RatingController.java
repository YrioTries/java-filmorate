package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.RatingService;
import ru.yandex.practicum.filmorate.model.Rating;

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
    public List<Rating> getAllRatings() {
        log.info("Запрос на получение списка всех рейтингов");
        return RatingService.getAllRatings();
    }

    @GetMapping("/{id}")
    public Rating getRatingById(@PathVariable Integer id) {
        log.info("Запрос на получение рейтинга по ID");
        return RatingService.getRatingById(id);
    }
}
