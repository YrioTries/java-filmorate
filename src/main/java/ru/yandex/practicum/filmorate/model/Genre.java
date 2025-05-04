package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Genre {
    @Id
    private Long id;

    @NotEmpty
    @NotBlank
    private String name;

    public Genre(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}