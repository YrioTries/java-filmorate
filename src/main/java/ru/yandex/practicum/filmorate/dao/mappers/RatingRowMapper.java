package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingRowMapper implements RowMapper<Rating> {

    @Override
    public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
        Integer mpaId = rs.getInt("mpa_rating_id");
        if (mpaId == 0) {
            mpaId = null;
        }
        return new Rating(
                mpaId,
                rs.getString("name")
        );
    }
}
