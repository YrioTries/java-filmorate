package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Date;

@Data
public class User {
    Long id;
    @NotNull
    @NotBlank
    String email;

    @NotNull
    @NotBlank
    String login;
    String name;

    @NotNull
    @NotBlank
    Date birthday;
}
