package ru.yandex.practicum.filmorate.storage.dao.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Component
@Qualifier("SQL_Film_Storage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Long> getFilmsKeys() {
        String sql = "SELECT id FROM films";
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    @Override
    public Collection<Film> getFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public Film getFilm(Long id) {
        String sql = "SELECT * FROM films WHERE id = ?";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        if (films.isEmpty()) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        return films.get(0);
    }

    @Override
    public Film create(Film film) {
        // Проверяем, существует ли рейтинг (mpa)
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new IllegalArgumentException("Рейтинг (mpa) не может быть null.");
        }

        // Проверяем, существует ли рейтинг с указанным id
        String checkRatingSql = "SELECT COUNT(*) FROM ratings WHERE id = ?";
        int ratingCount = jdbcTemplate.queryForObject(checkRatingSql, Integer.class, film.getMpa().getId());
        if (ratingCount == 0) {
            throw new NotFoundException("Рейтинг с id = " + film.getMpa().getId() + " не найден.");
        }

        // Проверяем, существуют ли жанры
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                String checkGenreSql = "SELECT COUNT(*) FROM genres WHERE id = ?";
                int genreCount = jdbcTemplate.queryForObject(checkGenreSql, Integer.class, genre.getId());
                if (genreCount == 0) {
                    throw new NotFoundException("Жанр с id = " + genre.getId() + " не найден.");
                }
            }
        }

        String sql = "INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setLong(5, film.getMpa().getId()); // Используем getId()
            return stmt;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        saveFilmGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        // Проверяем, существует ли рейтинг (mpa)
        if (film.getMpa() == null || film.getMpa().getId() == null) {
            throw new IllegalArgumentException("Рейтинг (mpa) не может быть null.");
        }

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(), // Используем getId()
                film.getId());
        if (updated == 0) {
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }
        saveFilmGenres(film);
        return film;
    }

    @Override
    public boolean likeFilm(Long filmId, Long userId) {
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

        // Получаем рейтинг фильма
        String ratingSql = "SELECT * FROM ratings WHERE id = ?";
        Rating mpa = jdbcTemplate.queryForObject(ratingSql, (rs2, rowNum2) ->
                new Rating(rs2.getLong("id"), rs2.getString("name"), rs2.getString("description")), rs.getLong("rating_id"));
        film.setMpa(mpa); // Устанавливаем рейтинг

        // Получаем жанры фильма
        String genresSql = "SELECT g.id AS genre_id, g.name AS genre_name FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.id WHERE fg.film_id = ?";
        List<Map<String, Object>> genreMaps = jdbcTemplate.queryForList(genresSql, film.getId());
        if (!genreMaps.isEmpty()) {
            Set<Genre> genres = new HashSet<>();
            for (Map<String, Object> genreMap : genreMaps) {
                Long genreId = (Long) genreMap.get("genre_id");
                String genreName = (String) genreMap.get("genre_name");
                genres.add(new Genre(genreId, genreName));
            }
            film.setGenres(genres);
        }

        // Получаем лайки фильма
        String likesSql = "SELECT user_id FROM likes WHERE film_id = ?";
        Set<Long> likes = new HashSet<>(
                jdbcTemplate.queryForList(likesSql, Long.class, film.getId()));
        film.setLikesFrom(likes);

        return film;
    }

    private void saveFilmGenres(Film film) {
        // Удаляем все текущие жанры фильма
        String deleteSql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(deleteSql, film.getId());

        // Сохраняем новые жанры фильма
        if (film.getGenres() != null) {
            String insertSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(insertSql, film.getId(), genre.getId());
            }
        }
    }
}
