package ru.yandex.practicum.filmorate.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.Collection;
import java.util.Optional;

@Repository
public class RatingDbStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Rating> findAll() {
        String sql = "SELECT * FROM ratings";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Rating(rs.getLong("id"), rs.getString("name"), rs.getString("description")));
    }

    @Override
    public Optional<Rating> getRating(Long id) {
        String sql = "SELECT * FROM ratings WHERE id = ?";
        return jdbcTemplate.query(sql, rs -> rs.next() ? Optional.of(new Rating(rs.getLong("id"), rs.getString("name"), rs.getString("description"))) : Optional.empty(), id);
    }
}