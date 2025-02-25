package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private long id;

    @NotEmpty
    @NotBlank
    @Email
    private String email;

    @NotEmpty
    @NotBlank
    private String login;
    private String name;

    @PastOrPresent
    private LocalDate birthday;

    @NotNull
    private Set<Long> friends = new HashSet<>(); // Инициализация коллекции

    public User() {
        // Конструктор по умолчанию
    }

    public User(long id, @NotNull String email, @NotNull String login, String name, @NotNull LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public User(long id, @NotNull String email, @NotNull String login, String name, @NotNull LocalDate birthday, Set<Long> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = (friends != null) ? new HashSet<>(friends) : new HashSet<>();
    }

}
