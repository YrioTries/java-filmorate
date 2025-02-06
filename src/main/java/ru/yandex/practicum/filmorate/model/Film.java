package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;

@Data
public class Film {

    private long id;

    @NotBlank
    @NotEmpty
    private String name;

    @Size(max = 200)
    private String description;

    private Instant releaseDate;

    @Positive
    private long duration;
}
