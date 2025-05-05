package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    @Id
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;
    private Set<Genre> genres;
    private Rating mpa;
    private Set<Long> likesFrom;


    public Film() {
        this.likesFrom = new HashSet<>();
        this.genres = new HashSet<>();
    }

    public Film(String name, String description, LocalDate releaseDate, long duration) {
        this();
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public Film(String name, String description, LocalDate releaseDate, long duration, Set<Long> likesFrom) {
        this(name, description, releaseDate, duration);
        this.likesFrom = new HashSet<>(likesFrom);
    }

    // Новый конструктор со всеми полями
    public Film(Long id, String name, String description, LocalDate releaseDate,
                long duration, Rating mpa, Set<Genre> genres, Set<Long> likesFrom) {
        this(name, description, releaseDate, duration);
        this.id = id;
        this.mpa = mpa;
        this.likesFrom = likesFrom != null ? new HashSet<>(likesFrom) : new HashSet<>();
        this.genres = genres != null ? new HashSet<>(genres) : new HashSet<>();
    }

    public void setGenre(Genre genre) {
        this.genres.add(genre);
    }
}