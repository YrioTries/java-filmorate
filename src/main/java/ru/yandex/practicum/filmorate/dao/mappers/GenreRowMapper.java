package ru.yandex.practicum.filmorate.dao.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class GenreRowMapper implements RowMapper<Genre> {

    @Override
    public Genre mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Integer genreId = resultSet.getInt("genre_id");
        if (genreId == 0) {
            genreId = null;
        }
        return new Genre(
                genreId,
                resultSet.getString("name")
        );
    }
}
