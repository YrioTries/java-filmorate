package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class RatingRowMapper implements RowMapper<Rating> {

    @Override
    public Rating mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Integer ratingId = resultSet.getInt("rating_id");
        if (ratingId == 0) {
            ratingId = null;
        }
        return new Rating(
                ratingId,
                resultSet.getString("name")
        );
    }
}
