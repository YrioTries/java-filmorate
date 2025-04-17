package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.yandex.practicum.filmorate.enums.film.Genre;
import ru.yandex.practicum.filmorate.enums.film.Rating;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private long id;

    @NotBlank
    @NotNull
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    @PastOrPresent(message = "Дата не может быть в будущем")
    private LocalDate releaseDate;

    @Positive
    private long duration;

    private Genre genre;

    private Rating rating;

    @NotNull
    private Set<Long> likesFrom; // Инициализация коллекции

    public Film() {
        likesFrom = new HashSet<>();
    }

    public Film(@NotNull String name, String description, @NotNull LocalDate releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likesFrom = new HashSet<>();
    }

    public Film(@NotNull String name, String description, @NotNull LocalDate releaseDate, long duration, Set<Long> likesFrom) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likesFrom = new HashSet<>(likesFrom);
    }
}
