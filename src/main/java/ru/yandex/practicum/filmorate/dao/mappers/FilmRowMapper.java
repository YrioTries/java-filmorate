package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashSet;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer ratingId = rs.getInt("mpa_rating_id");
        if (ratingId == 0) {
            ratingId = null;
        }
        Rating mpa = new Rating(
                ratingId,
                rs.getString("mpa_rating_name")
        );

        return new Film(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                Collections.unmodifiableSequencedSet(new LinkedHashSet<>()),
                mpa
        );
    }
}
