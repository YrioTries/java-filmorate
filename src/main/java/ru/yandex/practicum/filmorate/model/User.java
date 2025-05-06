package ru.yandex.practicum.filmorate.model;

import lombok.Value;


import java.time.LocalDate;

@Value
public class User {
    Long id;
    String name;
    String email;
    String login;
    LocalDate birthday;
}
