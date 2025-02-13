package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
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
    private LocalDate releaseDate;

    @Positive
    private long duration;

    Set<Long>likesFrom;
}
