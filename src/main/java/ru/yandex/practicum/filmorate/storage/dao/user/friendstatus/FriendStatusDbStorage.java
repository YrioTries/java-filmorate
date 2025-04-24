package ru.yandex.practicum.filmorate.storage.dao.user.friendstatus;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.FriendStatus;

import java.util.Collection;
import java.util.Optional;

@Component
public class FriendStatusDbStorage implements FriendStatusStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FriendStatusDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<FriendStatus> findAll() {
        String sql = "SELECT * FROM friendship_statuses";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new FriendStatus(rs.getLong("id"), rs.getString("status")));
    }

    @Override
    public Optional<FriendStatus> getFriendStatus(Long id) {
        String sql = "SELECT * FROM friendship_statuses WHERE id = ?";
        return jdbcTemplate.query(sql, rs -> rs.next() ? Optional.of(new FriendStatus(rs.getLong("id"), rs.getString("status"))) : Optional.empty(), id);
    }
}

