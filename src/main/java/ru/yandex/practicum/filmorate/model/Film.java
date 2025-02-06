package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;

@Data
public class Film {
    @NotNull
    @PositiveOrZero
    private long id;

    @NotBlank
    @NotEmpty
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    private Instant releaseDate;

    @Positive
    private long duration;
}
