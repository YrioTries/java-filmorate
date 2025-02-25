package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

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
    @PastOrPresent // Проверка, чтобы дата была не в будущем
    private LocalDate releaseDate;

    @Positive
    private long duration;

    @NotNull
    private Set<Long> likesFrom = new HashSet<>(); // Инициализация коллекции

    public Film() {
        // Конструктор по умолчанию
    }

    public Film(long id, @NotNull String name, String description, @NotNull LocalDate releaseDate, long duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    // Дополнительный конструктор для полной инициализации
    public Film(long id, @NotNull String name, String description, @NotNull LocalDate releaseDate, long duration, Set<Long> likesFrom) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likesFrom = new HashSet<>();
    }
}
