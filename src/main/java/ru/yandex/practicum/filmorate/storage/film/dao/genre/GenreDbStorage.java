package ru.yandex.practicum.filmorate.storage.film.dao.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.enums.film.Genre;
import ru.yandex.practicum.filmorate.storage.film.dao.genre.GenreStorage;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Optional;

@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> findAll() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, rs -> {
            Collection<Genre> genres = EnumSet.noneOf(Genre.class);
            while (rs.next()) {
                genres.add(Genre.valueOf(rs.getString("name")));
            }
            return genres;
        });
    }

    @Override
    public Optional<Genre> getGenre(Long id) {
        String sql = "SELECT * FROM genres WHERE id = ?";
        return jdbcTemplate.query(sql, rs -> rs.next() ? Optional.of(Genre.valueOf(rs.getString("name"))) : Optional.empty(), id);
    }
}
