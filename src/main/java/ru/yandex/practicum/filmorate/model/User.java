package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class User {

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
    private LocalDate birthday;

    private Set<Long> friends;
}
