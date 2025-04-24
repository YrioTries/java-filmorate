package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.Collection;

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
        return ratingService.findAll();
    }

    @GetMapping("/{id}")
    public Rating get(@PathVariable Long id) {
        return ratingService.get(id).orElseThrow(() -> new NotFoundException("Рейтинг не найден"));
    }
}