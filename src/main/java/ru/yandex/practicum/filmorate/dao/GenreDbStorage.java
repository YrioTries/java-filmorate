package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Primary
@Repository
public class GenreDbStorage implements GenreStorage {
    private final JdbcOperations jdbc;
    private final RowMapper<Genre> mapper;

    @Autowired
    public GenreDbStorage(JdbcOperations jdbc, RowMapper<Genre> mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public List<Genre> getAllGenres() {
        final String FIND_ALL_QUERY = """
                SELECT *
                FROM genres;
                """;

        List<Genre> genres = jdbc.query(FIND_ALL_QUERY, mapper);
        if (genres == null || genres.isEmpty()) {
            return List.of();
        }
        return genres;
    }

    @Override
    public Genre getGenreById(Integer id) {
        final String FIND_BY_ID_QUERY = """
                SELECT *
                FROM genres
                WHERE genre_id = ?;
                """;

        Genre genre;
        try {
            genre = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            genre = null;
        }
        if (genre == null) {
            log.warn("GenreDbStorage: Не удалось получить объект Genre по его ID - не найден в приложении");
            throw new NotFoundException("GenreDbStorage: Жанр c ID: " + id + " не найден в приложении");
        }
        return genre;
    }
}
