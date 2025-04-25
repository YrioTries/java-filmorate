package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private Rating rating;
    private Set<Long> likesFrom;
    private Set<Genre> genres; // Добавьте поле для жанров

    public Film() {
        likesFrom = new HashSet<>();
        genres = new HashSet<>(); // Инициализируем коллекцию жанров
    }

    public Film(String name, String description, LocalDate releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likesFrom = new HashSet<>();
        this.genres = new HashSet<>(); // Инициализируем коллекцию жанров
    }

    public Film(String name, String description, LocalDate releaseDate, long duration, Set<Long> likesFrom) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likesFrom = new HashSet<>(likesFrom);
        this.genres = new HashSet<>(); // Инициализируем коллекцию жанров
    }

    public void setGenre(Genre genre) {
        this.genres.add(genre);
    }

}
