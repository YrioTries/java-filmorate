package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.SequencedSet;

@Value
public class Film {
    @Id
    Long id;
    @NotBlank
    @NotNull
    String name;
    @Size(max = 200)
    String description;
    @NotNull
    @PastOrPresent(message = "Дата не может быть в будущем")
    LocalDate releaseDate;
    @Positive
    int duration;
    SequencedSet<Genre> genres;
    Rating mpa;
}
