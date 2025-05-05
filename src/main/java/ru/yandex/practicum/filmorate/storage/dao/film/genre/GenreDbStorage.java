package ru.yandex.practicum.filmorate.storage.dao.film.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAll() {
        log.info("Получение всех жанров из базы данных");
        String sql = "SELECT * FROM genres ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name")));
    }

    @Override
    public Optional<Genre> getGenre(Long id) {
        log.info("Получение жанра с id: {} из базы данных", id);
        String sql = "SELECT * FROM genres WHERE id = ?";
        return jdbcTemplate.query(sql, rs -> rs.next() ? Optional.of(new Genre(rs.getLong("id"), rs.getString("name"))) : Optional.empty(), id);
    }
}
