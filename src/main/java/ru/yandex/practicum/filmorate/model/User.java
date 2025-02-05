package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    @PositiveOrZero
    private long id;

    @NotEmpty
    @NotBlank
    @Email
    String email;

    @NotEmpty
    @NotBlank
    String login;
    String name;

    @PastOrPresent
    LocalDate birthday;
}
