package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Value
public class User {
    @Id
    Long id;
    String name;
    @Email
    @NotEmpty
    @NotBlank
    String email;
    String login;
    @Past
    LocalDate birthday;
}
