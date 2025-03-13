package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class IdValue {
    private Long id; // Используем Long, т.к. friendId - Long

    public IdValue(Long id) {
        this.id = id;
    }
}
