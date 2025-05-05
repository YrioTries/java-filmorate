package ru.yandex.practicum.filmorate.storage.dao.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
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
        final String INSERT_FILM_QUERY = """
                INSERT INTO films (name, description, release_date, duration, rating_id)
                VALUES (?, ?, ?, ?, ?);
                """;
        final String INSERT_FILM_ID_GENRES_IDS_QUERY = """
                INSERT INTO film_genre (film_id, genre_id)
                VALUES (?, ?);
                """;

        final Object[] params = {
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        };

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_FILM_QUERY, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);
        Long generatedId = keyHolder.getKeyAs(Long.class);
        if (generatedId == null) {
            throw new InternalServerException("FilmDbStorage: Не удалось сохранить данные Film");
        }

        if (!(film.getGenres().isEmpty())) {
            List<Long> genresIds = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList();

            jdbcTemplate.batchUpdate(INSERT_FILM_ID_GENRES_IDS_QUERY,
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            Long genreId = genresIds.get(i);
                            ps.setLong(1, generatedId);
                            ps.setLong(2, genreId);
                        }

                        public int getBatchSize() {
                            return genresIds.size();
                        }
                    });
        }

        return new Film(
                generatedId,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa(),
                film.getGenres(),
                film.getLikesFrom()
        );
    }

    @Override
    public Film update(Film film) {
        log.info("Обновление фильма с id: {}", film.getId());
        final String UPDATE_FILM_QUERY = """
                UPDATE films
                SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ?
                WHERE id = ?;
                """;
        final String DELETE_GENRES_QUERY = """
                DELETE FROM film_genre
                WHERE film_id = ?;
                """;
        final String INSERT_FILM_ID_GENRES_IDS_QUERY = """
                INSERT INTO film_genre (film_id, genre_id)
                VALUES (?, ?);
                """;

        final Object[] params = {
                film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa(),
                film.getGenres(),
                film.getLikesFrom()
        };

        int rowsUpdated = jdbcTemplate.update(UPDATE_FILM_QUERY, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("FilmDbStorage: Не удалось обновить данные Film");
        }


        int rowsDeleted = jdbcTemplate.update(DELETE_GENRES_QUERY, film.getId());
        if (rowsDeleted == 0) {
            log.info("FilmDbStorage: Не удалось удалить genres у Film с ID: {}", film.getId());
        }

        if (!(film.getGenres().isEmpty())) {
            List<Long> genresIds = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList();

            jdbcTemplate.batchUpdate(INSERT_FILM_ID_GENRES_IDS_QUERY,
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            Long genreId = genresIds.get(i);
                            ps.setLong(1, film.getId());
                            ps.setLong(2, genreId);
                        }

                        public int getBatchSize() {
                            return genresIds.size();
                        }
                    });
        }
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
            log.error("Ошибка при добавлении лайка", e);
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

        Long mpaId = rs.getObject("mpa_id", Long.class);
        Rating mpa = null;
        if (mpaId != null) {
            try {
                String ratingSql = "SELECT * FROM ratings WHERE id = ?";
                mpa = jdbcTemplate.queryForObject(ratingSql, (rs2, rowNum2) ->
                                new Rating(
                                        rs2.getLong("id"),
                                        rs2.getString("name"),
                                        rs2.getString("description")
                                ),
                        mpaId);
            } catch (EmptyResultDataAccessException e) {
                log.error("Рейтинг с id: {} не найден в базе данных", mpaId);
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
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

        List<Object[]> batchArgs = film.getGenres().stream()
                .map(genre -> new Object[]{film.getId(), genre.getId()})
                .toList();

        jdbcTemplate.batchUpdate(sql, batchArgs);
        log.info("Сохранены {} жанров для фильма с id: {}", batchArgs.size(), film.getId());
    }
}