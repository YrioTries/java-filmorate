package ru.yandex.practicum.filmorate.dao.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Slf4j
@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcOperations jdbc;

    private final RowMapper<Film> mapper;

    private final static String PROGRAM_LEVEL = "FilmDbStorage";

    @Autowired
    public FilmDbStorage(final JdbcOperations jdbc, final RowMapper<Film> mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public List<Film> getAllFilms() {
        final String FIND_ALL_FILMS_WITH_MPA_RATING_QUERY = """
                SELECT f.*, mr.name AS mpa_rating_name
                FROM films AS f
                LEFT OUTER JOIN mpa_rating AS mr
                ON f.mpa_rating_id = mr.mpa_rating_id
                """;
        final String FIND_ALL_FILMS_IDS_WITH_GENRES_QUERY = """
                SELECT fg.film_id, fg.genre_id, g.name AS genre_name
                FROM film_genre AS fg
                LEFT OUTER JOIN genres AS g ON fg.genre_id = g.genre_id
                """;

        List<Film> tmpFilms = jdbc.query(FIND_ALL_FILMS_WITH_MPA_RATING_QUERY, mapper);
        if (tmpFilms == null || tmpFilms.isEmpty()) {
            return List.of();
        }

        Map<Long, SequencedSet<Genre>> filmsGenres = jdbc.query(FIND_ALL_FILMS_IDS_WITH_GENRES_QUERY,
                new FilmDbStorage.FilmsIdsWithGenresExtractor());

        List<Film> films = new ArrayList<>();
        for (Film tmpFilm : tmpFilms) {
            if (filmsGenres.containsKey(tmpFilm.getId())) {
                Film film = new Film(
                        tmpFilm.getId(),
                        tmpFilm.getName(),
                        tmpFilm.getDescription(),
                        tmpFilm.getReleaseDate(),
                        tmpFilm.getDuration(),
                        Collections.unmodifiableSequencedSet(filmsGenres.get(tmpFilm.getId())),
                        tmpFilm.getMpa()
                );
                films.add(film);
            } else {
                films.add(tmpFilm);
            }
        }
        return films;
    }

    @Override
    public Film getFilmById(Long id) {
        final String FIND_FILM_BY_ID_WITH_MPA_AND_GENRES_QUERY = """
                SELECT f.*, mr.name AS mpa_rating_name, fg.genre_id, g.name AS genre_name
                FROM films AS f
                LEFT OUTER JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id
                LEFT OUTER JOIN film_genre AS fg ON f.id = fg.film_id
                LEFT OUTER JOIN genres AS g ON fg.genre_id = g.genre_id
                WHERE f.id = ?;
                """;

        Optional<Film> optionalFilm = jdbc.query(FIND_FILM_BY_ID_WITH_MPA_AND_GENRES_QUERY,
                new FilmWithRatingAndGenresExtractor(), id);

        if (optionalFilm.isEmpty()) {
            log.warn(PROGRAM_LEVEL + ": Не удалось получить объект Film по его ID - не найден в приложении");
            throw new NotFoundException("FilmDbStorage: Фильм c ID: " + id + " не найден");
        }
        return optionalFilm.get();
    }

    @Override
    public Film create(Film film) {
        final String INSERT_FILM_QUERY = """
                INSERT INTO films (name, description, release_date, duration, mpa_rating_id)
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
        jdbc.update(connection -> {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(INSERT_FILM_QUERY, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                preparedStatement.setObject(idx + 1, params[idx]);
            }
            return preparedStatement;
        }, keyHolder);
        Long generatedId = keyHolder.getKeyAs(Long.class);
        if (generatedId == null) {
            throw new InternalServerException(PROGRAM_LEVEL + ": Не удалось сохранить данные Film");
        }

        if (!(film.getGenres().isEmpty())) {
            List<Integer> genresIds = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList();

            jdbc.batchUpdate(INSERT_FILM_ID_GENRES_IDS_QUERY,
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            Integer genreId = genresIds.get(i);
                            ps.setLong(1, generatedId);
                            ps.setInt(2, genreId);
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
                film.getGenres(),
                film.getMpa()
        );
    }

    @Override
    public Film update(Film film) {
        final String UPDATE_FILM_QUERY = """
                UPDATE films
                SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?
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
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        };


        int updatedRows = jdbc.update(UPDATE_FILM_QUERY, params);
        if (updatedRows == 0) {
            throw new InternalServerException(PROGRAM_LEVEL + ": Не удалось обновить данные Film");
        }

        int deletedRows = jdbc.update(DELETE_GENRES_QUERY, film.getId());
        if (deletedRows == 0) {
            log.info(PROGRAM_LEVEL + ": Не удалось удалить genres у Film с ID: {}", film.getId());
        }

        if (!(film.getGenres().isEmpty())) {
            List<Integer> genresIds = film.getGenres()
                    .stream()
                    .map(Genre::getId)
                    .toList();

            jdbc.batchUpdate(INSERT_FILM_ID_GENRES_IDS_QUERY,
                    new BatchPreparedStatementSetter() {
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            Integer genreId = genresIds.get(i);
                            ps.setLong(1, film.getId());
                            ps.setInt(2, genreId);
                        }

                        public int getBatchSize() {
                            return genresIds.size();
                        }
                    });
        }
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        final String INSERT_FILM_LIKE_QUERY = """
                INSERT INTO film_like (film_id, user_id)
                VALUES (?, ?);
                """;

        final Object[] params = {
                filmId,
                userId
        };

        jdbc.update(INSERT_FILM_LIKE_QUERY, params);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        final String DELETE_FILM_LIKE_QUERY = """
                DELETE FROM film_like
                WHERE film_id = ? AND user_id = ?;
                """;

        int rowsDeleted = jdbc.update(DELETE_FILM_LIKE_QUERY, filmId, userId);
        if (rowsDeleted == 0) {
            log.info(PROGRAM_LEVEL + ": Не удалось удалить like у Film с ID: {}", filmId);
        }
    }

    @Override
    public List<Film> getTopFilms(int limit) {
        final String FIND_FILMS_WITH_MPA_RATING_SORTED_BY_LIKES_LIMITED_QUERY = """
                SELECT f.*, mr.name AS mpa_rating_name
                FROM films AS f
                LEFT OUTER JOIN mpa_rating AS mr ON f.mpa_rating_id = mr.mpa_rating_id
                LEFT OUTER JOIN film_like AS fl ON f.id = fl.film_id
                GROUP BY f.id
                ORDER BY COUNT(fl.user_id) DESC
                LIMIT ?;
                """;
        final String FIND_FILMS_IDS_WITH_GENRES_SORTED_BY_LIKES_LIMITED_QUERY = """
                SELECT f.id AS film_id, fg.genre_id, g.name AS genre_name
                FROM films AS f
                LEFT OUTER JOIN film_like AS fl ON f.id = fl.film_id
                LEFT OUTER JOIN film_genre AS fg ON f.id = fg.film_id
                LEFT OUTER JOIN genres AS g ON fg.genre_id = g.genre_id
                GROUP BY f.id, fg.genre_id
                ORDER BY COUNT(fl.user_id) DESC, f.id
                LIMIT ?;
                """;

        List<Film> sortFilms = jdbc.query(FIND_FILMS_WITH_MPA_RATING_SORTED_BY_LIKES_LIMITED_QUERY, mapper, limit);
        if (sortFilms == null || sortFilms.isEmpty()) {
            return List.of();
        }

        Map<Long, SequencedSet<Genre>> filmsGenres = jdbc.query(FIND_FILMS_IDS_WITH_GENRES_SORTED_BY_LIKES_LIMITED_QUERY,
                new FilmDbStorage.FilmsIdsWithGenresExtractor(), limit);

        List<Film> films = new ArrayList<>();
        for (Film sortFilm : sortFilms) {
            Film film = new Film(
                    sortFilm.getId(),
                    sortFilm.getName(),
                    sortFilm.getDescription(),
                    sortFilm.getReleaseDate(),
                    sortFilm.getDuration(),
                    Collections.unmodifiableSequencedSet(filmsGenres.get(sortFilm.getId())),
                    sortFilm.getMpa()
            );
            films.add(film);
        }
        return films;
    }

    private static class FilmWithRatingAndGenresExtractor implements ResultSetExtractor<Optional<Film>> {
        @Override
        public Optional<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Film tmpFilm = null;
            SequencedSet<Genre> genres = new LinkedHashSet<>();
            while (rs.next()) {
                if (tmpFilm == null) {

                    Integer ratingId = rs.getInt("mpa_rating_id");
                    if (ratingId == 0) {
                        ratingId = null;
                    }
                    Rating mpa = new Rating(
                            ratingId,
                            rs.getString("mpa_rating_name")
                    );

                    tmpFilm = new Film(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getDate("release_date").toLocalDate(),
                            rs.getInt("duration"),
                            Collections.unmodifiableSequencedSet(new LinkedHashSet<>()),
                            mpa
                    );
                }

                int genreId = rs.getInt("genre_id");
                String genreName = rs.getString("genre_name");
                if (genreId != 0) {
                    Genre genre = new Genre(genreId, genreName);
                    genres.add(genre);
                }
            }

            if ((tmpFilm != null) && (!genres.isEmpty())) {
                Film film = new Film(
                        tmpFilm.getId(),
                        tmpFilm.getName(),
                        tmpFilm.getDescription(),
                        tmpFilm.getReleaseDate(),
                        tmpFilm.getDuration(),
                        Collections.unmodifiableSequencedSet(genres),
                        tmpFilm.getMpa()
                );
                return Optional.of(film);
            }
            return Optional.ofNullable(tmpFilm);
        }
    }

    private static class FilmsIdsWithGenresExtractor implements ResultSetExtractor<Map<Long, SequencedSet<Genre>>> {
        @Override
        public Map<Long, SequencedSet<Genre>> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
            Map<Long, SequencedSet<Genre>> data = new HashMap<>();
            Genre genre;
            while (resultSet.next()) {
                Long filmId = resultSet.getLong("film_id");
                data.putIfAbsent(filmId, new LinkedHashSet<>());
                //genre
                int genreId = resultSet.getInt("genre_id");
                String genreName = resultSet.getString("genre_name");
                if (genreId != 0) {
                    genre = new Genre(genreId, genreName);
                    data.get(filmId).add(genre);
                }
            }
            return data;
        }
    }
}
