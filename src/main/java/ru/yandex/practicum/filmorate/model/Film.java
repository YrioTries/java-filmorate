package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.Duration;
import java.time.Instant;

@Data
public class Film {
    Long id;
    String name;
    String description;
    Instant releaseDate;
    Duration duration;
}
