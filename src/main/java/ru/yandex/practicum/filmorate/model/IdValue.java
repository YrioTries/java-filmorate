package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class IdValue {
    private Long id;

    public IdValue(Long id) {
        this.id = id;
    }
}
