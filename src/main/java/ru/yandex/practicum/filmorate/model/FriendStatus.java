package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class FriendStatus {
    private Long id;
    private String status;

    public FriendStatus(Long id, String status) {
        this.id = id;
        this.status = status;
    }
}
