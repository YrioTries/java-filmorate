package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.List;

@Slf4j
@Primary
@Repository
public class RatingDbStorage implements RatingStorage {
    private final JdbcOperations jdbc;
    private final RowMapper<Rating> mapper;

    @Autowired
    public RatingDbStorage(JdbcOperations jdbc, RowMapper<Rating> mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public List<Rating> getAllRatings() {
        final String FIND_ALL_QUERY = """
                SELECT *
                FROM rating;
                """;

        List<Rating> mpaRatings = jdbc.query(FIND_ALL_QUERY, mapper);
        if (mpaRatings == null || mpaRatings.isEmpty()) {
            return List.of();
        }
        return mpaRatings;
    }

    @Override
    public Rating getRatingById(Integer id) {
        final String FIND_BY_ID_QUERY = """
                SELECT *
                FROM rating
                WHERE rating_id = ?;
                """;

        Rating mpa;
        try {
            mpa = jdbc.queryForObject(FIND_BY_ID_QUERY, mapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            mpa = null;
        }
        if (mpa == null) {
            log.warn("MpaRatingDbStorage: Не удалось получить объект MpaRating по его ID - не найден в приложении");
            throw new NotFoundException("MpaRatingDbStorage: Рейтинг c ID: " + id + " не найден в приложении");
        }
        return mpa;
    }
}