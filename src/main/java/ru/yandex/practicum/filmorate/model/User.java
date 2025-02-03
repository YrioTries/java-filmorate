package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class User {
    @PositiveOrZero
    Long id;
    @NotEmpty
    @NotBlank
    @Email
    String email;

    @NotEmpty
    @NotBlank
    String login;
    String name;

    @NotEmpty
    @NotBlank
    Date birthday;
}
