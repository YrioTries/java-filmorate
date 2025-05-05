package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public Collection<Genre> findAll() {
        log.info("GET запрос на получение всех жанров");
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    public Genre get(@PathVariable Long id) {
        log.info("GET запрос на получение жанра с id: {}", id);
        return genreService.get(id).orElseThrow(() -> new NotFoundException("Жанр не найден"));
    }
}
