package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class User {

    @PositiveOrZero
    private long id;

    @NotEmpty
    @NotBlank
    @Email
    private String email;

    @NotEmpty
    @NotBlank
    private String login;
    private String name;

    @NotEmpty
    @NotBlank
    private Date birthday;
}
