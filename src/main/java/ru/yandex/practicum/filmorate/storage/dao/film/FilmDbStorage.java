package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Component
@Slf4j
@Qualifier("SQL_Film_Storage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Long> getFilmsKeys() {
        log.info("Получение всех ключей фильмов из базы данных");
        String sql = "SELECT id FROM films";
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    @Override
    public Collection<Film> getFilms() {
        log.info("Получение всех фильмов из базы данных");
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film getFilm(Long id) {
        log.info("Получение фильма с id: {} из базы данных", id);
        String sql = "SELECT * FROM films WHERE id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        if (films.isEmpty()) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        return films.get(0);
    }

    @Override
    public Film create(Film film) {
        log.info("Создание нового фильма: {}", film);
        String sql = """
            INSERT INTO films (name, description, release, duration, mpa_id)
            VALUES (:name, :description, :release, :duration, :mpa_id)""";
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        params.addValue("mpa_id", (film.getMpa() == null) ? null : film.getMpa().getId());
        jdbcTemplate.update(sql, params, keyHolder);
        film.setId(keyHolder.getKeyAs(Long.class));

        if (!film.getGenres().isEmpty()) {
            saveFilmGenres(film);
        }
        log.info("Создан новый фильм с id: {}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        log.info("Обновление фильма с id: {}", film.getId());
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new IllegalArgumentException("Рейтинг (mpa) не может быть null.");
        }

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (updated == 0) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        saveFilmGenres(film);
        log.info("Фильм с id: {} обновлен", film.getId());
        return film;
    }

    @Override
    public boolean likeFilm(Long filmId, Long userId) {
        log.info("Добавление лайка фильму с id: {} от пользователя с id: {}", filmId, userId);
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, filmId, userId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean unLikeFilm(Long filmId, Long userId) {
        log.info("Удаление лайка с фильма с id: {} от пользователя с id: {}", filmId, userId);
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        return jdbcTemplate.update(sql, filmId, userId) > 0;
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getLong("duration"));

        Long ratingId = rs.getObject("rating_id", Long.class);
        Rating mpa = null;
        if (ratingId != null) {
            try {
                String ratingSql = "SELECT * FROM ratings WHERE id = ?";
                mpa = jdbcTemplate.queryForObject(ratingSql, (rs2, rowNum2) ->
                                new Rating(
                                        rs2.getLong("id"),
                                        rs2.getString("name"),
                                        rs2.getString("description")
                                ),
                        ratingId);
            } catch (EmptyResultDataAccessException e) {
                log.error("Рейтинг с id: {} не найден в базе данных", ratingId);
            }
        }
        film.setMpa(mpa);

        String genresSql = "SELECT g.id AS genre_id, g.name AS genre_name " +
                "FROM film_genres fg JOIN genres g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ?";
        List<Genre> genres = jdbcTemplate.query(genresSql, (rs3, rowNum3) ->
                        new Genre(rs3.getLong("genre_id"), rs3.getString("genre_name")),
                film.getId());
        film.setGenres(new HashSet<>(genres));

        String likesSql = "SELECT user_id FROM likes WHERE film_id = ?";
        Set<Long> likes = new HashSet<>(
                jdbcTemplate.queryForList(likesSql, Long.class, film.getId()));
        film.setLikesFrom(likes);

        return film;
    }

    private void saveFilmGenres(Film film) {
        log.info("Сохранение жанров для фильма с id: {}", film.getId());
        String sql = """
                INSERT INTO film_genre (film_id, genre_id)
                VALUES (:film_id, :genre_id)""";
        MapSqlParameterSource[] batchArgs = film.getGenres().stream()
                .map(genre -> {
                    MapSqlParameterSource params = new MapSqlParameterSource();
                    params.addValue("film_id", film.getId());
                    params.addValue("genre_id", genre.getId());
                    return params;
                })
                .toArray(MapSqlParameterSource[]::new);
        jdbcTemplate.batchUpdate(sql, Collections.singletonList(batchArgs));
        log.trace("Сохранены жанры для фильма с id: {}", film.getId());
    }
}
