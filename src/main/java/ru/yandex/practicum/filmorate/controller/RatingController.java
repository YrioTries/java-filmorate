package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class RatingController {
    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public Collection<Rating> findAll() {
        log.info("GET запрос на получение всех рейтингов");
        return ratingService.findAll();
    }

    @GetMapping("/{id}")
    public Rating get(@PathVariable Long id) {
        log.info("GET запрос на получение рейтинга с id: {}", id);
        return ratingService.get(id).orElseThrow(() -> new NotFoundException("Рейтинг не найден"));
    }
}
