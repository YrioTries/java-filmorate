package ru.yandex.practicum.filmorate.model;

import lombok.Value;

import java.time.LocalDate;
import java.util.SequencedSet;

@Value
public class Film {
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    SequencedSet<Genre> genres;
    Rating mpa;
}