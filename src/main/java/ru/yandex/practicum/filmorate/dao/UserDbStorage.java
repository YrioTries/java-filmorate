package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.util.*;

@Slf4j
@Repository
@Qualifier("SQL_User_Storage")
public class UserDbStorage implements UserStorage {

    private final JdbcOperations jdbc;

    private final RowMapper<User> mapper;

    @Autowired
    public UserDbStorage(final JdbcOperations jdbc, final RowMapper<User> mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    @Override
    public List<User> getFriendsById(Long userId) {
        final String FIND_USER_FRIENDS_BY_ID_QUERY = """
                SELECT *
                FROM users
                WHERE id IN (SELECT friend_id FROM user_friend WHERE user_id = ?);
                """;

        return jdbc.query(FIND_USER_FRIENDS_BY_ID_QUERY, mapper, userId);
    }

    @Override
    public Set<Long> getUserFriendsIdsById(Long userId) {
        final String FIND_USER_FRIENDS_IDS_BY_ID_QUERY = """
                SELECT friend_id
                FROM user_friend
                WHERE user_id = ?;
                """;

        return Set.copyOf(jdbc.queryForList(FIND_USER_FRIENDS_IDS_BY_ID_QUERY, Long.class, userId));
    }

    @Override
    public List<User> getAllUsers() {
        final String FIND_ALL_USERS_QUERY = """
                SELECT *
                FROM users;
                """;

        List<User> tmpUsers = jdbc.query(FIND_ALL_USERS_QUERY, mapper);
        if (tmpUsers == null || tmpUsers.isEmpty()) {
            return List.of();
        }
        return tmpUsers;
    }

    @Override
    public User getUserById(Long id) {
        final String FIND_USER_BY_ID_QUERY = """
                SELECT *
                FROM users
                WHERE id = ?;
                """;

        User user;
        try {
            user = jdbc.queryForObject(FIND_USER_BY_ID_QUERY, mapper, id);
        } catch (EmptyResultDataAccessException ignored) {
            user = null;
        }
        if (user == null) {
            log.warn("UserDbStorage: Не удалось получить объект User по его ID - не найден в приложении");
            throw new NotFoundException("UserDbStorage: User c ID: " + id + " не найден в приложении");
        }
        return user;
    }

    @Override
    public User create(User user) {
        log.info("Создание нового пользователя: {}", user);

        final String INSERT_USER_QUERY = """
                INSERT INTO users (name, email, login, birthday)
                VALUES (?, ?, ?, ?);
                """;

        final Object[] params = {
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        };

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);
        Long generatedId = keyHolder.getKeyAs(Long.class);
        if (generatedId == null) {
            throw new InternalServerException("UserDbStorage: Не удалось сохранить данные User");
        }

        return new User(
                generatedId,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
    }

    @Override
    public User update(User user) {
        log.info("Обновление пользователя с id: {}", user.getId());

        final String UPDATE_USER_QUERY = """
                UPDATE users SET name = ?, email = ?, login = ?, birthday = ?
                WHERE id = ?;
                """;

        final Object[] params = {
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getId()
        };

        int rowsUpdated = jdbc.update(UPDATE_USER_QUERY, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("BaseDbStorage: Не удалось обновить данные User");
        }
        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        log.info("Добавление дружбы между пользователями с id: {} и {}", userId, friendId);
        final String INSERT_USER_FRIEND_QUERY = """
                INSERT INTO user_friend (user_id, friend_id)
                VALUES (?, ?);
                """;

        jdbc.update(INSERT_USER_FRIEND_QUERY, userId, friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        log.info("Удаление дружбы между пользователями с id: {} и {}", userId, friendId);
        final String DELETE_USER_FRIEND_QUERY = """
                DELETE FROM user_friend
                WHERE user_id = ? AND friend_id = ?;
                """;

        int rowsDeleted = jdbc.update(DELETE_USER_FRIEND_QUERY, userId, friendId);
        if (rowsDeleted == 0) {
            log.info("UserDbStorage: Не удалось удалить друга User с ID: {}", userId);
        }
    }

    @Override
    public List<User> getUsersByIdSet(Set<Long> ids) {
        final String FIND_USERS_BY_IDS_QUERY = """
                SELECT *
                FROM users
                WHERE id IN (%s);
                """;
        final String sqlPlaceholders = String.join(",", Collections.nCopies(ids.size(), "?"));

        return jdbc.query(String.format(FIND_USERS_BY_IDS_QUERY, sqlPlaceholders), mapper, ids.toArray());
    }

    private static class FilmsIdsWithGenresExtractor implements ResultSetExtractor<Map<Long, SequencedSet<Genre>>> {
        @Override
        public Map<Long, SequencedSet<Genre>> extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Long, SequencedSet<Genre>> data = new HashMap<>();
            Genre genre;
            while (rs.next()) {
                // film id
                Long filmId = rs.getLong("film_id");
                data.putIfAbsent(filmId, new LinkedHashSet<>());
                //genre
                int genreId = rs.getInt("genre_id");
                String genreName = rs.getString("genre_name");
                if (genreId != 0) {
                    genre = new Genre(genreId, genreName);
                    data.get(filmId).add(genre);
                }
            }
            return data;
        }
    }
}