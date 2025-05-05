package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@Slf4j
@Validated
@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("GET запрос на получение всех фильмов");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film get(@PathVariable Long id) {
        log.info("GET запрос на получение фильма с  id: {}", id);
        return filmService.get(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("GET запрос на получение {} популярных фильмов", count);
        return filmService.getPopularFilms(count);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("POST - запрос на размещение фильма {} с id: {}", film, film.getId());
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("PUT - запрос на обновление фильма {} с id: {}", newFilm, newFilm.getId());
        return filmService.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean userLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("PUT - пользователь {} поставил лайк фильму {}", userId, id);
        return filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean unLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("DELETE - пользователь {} убрал лайк с фильма {}", userId, id);
        return filmService.unLikeFilm(id, userId);
    }
}
