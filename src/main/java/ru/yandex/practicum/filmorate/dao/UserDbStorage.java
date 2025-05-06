package ru.yandex.practicum.filmorate.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Slf4j
@Repository
@Qualifier("SQL_User_Storage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Long> findAllKeys() {
        log.info("Получение всех ключей пользователей из базы данных");
        String sql = "SELECT id FROM users";
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    @Override
    public Collection<Long> findAllFriends(Long id) {
        log.info("Получение всех друзей пользователя с id: {} из базы данных", id);
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, id);
    }

    @Override
    public Collection<User> findAll() {
        log.info("Получение всех пользователей из базы данных");
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User getUser(Long id) {
        log.info("Получение пользователя с id: {} из базы данных", id);
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, id);
        if (users.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return users.get(0);
    }

    @Override
    public User create(User user) {
        log.info("Создание нового пользователя: {}", user);
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Создан новый пользователь с id: {}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        log.info("Обновление пользователя с id: {}", user.getId());
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        int updated = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        if (updated == 0) {
            throw new NotFoundException("Пользователь с id = " + user.getId() + " не найден");
        }
        log.info("Пользователь с id: {} обновлен", user.getId());
        return user;
    }

    @Override
    public Optional<Long> getFriendshipStatus(Long userId, Long friendId) {
        log.info("Получение статуса дружбы между пользователями с id: {} и {}", userId, friendId);
        String sql = "SELECT status_id FROM friendships WHERE user_id = ? AND friend_id = ?";
        try {
            Long statusId = jdbcTemplate.queryForObject(sql, Long.class, userId, friendId);
            return Optional.ofNullable(statusId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void updateFriendshipStatus(Long userId, Long friendId, Long statusId) {
        log.info("Обновление статуса дружбы между пользователями с id: {} и {} на статус {}", userId, friendId, statusId);
        String sql = "UPDATE friendships SET status_id = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, statusId, userId, friendId);
    }

    @Override
    public void addFriendship(Long userId, Long friendId, Long statusId) {
        log.info("Добавление дружбы между пользователями с id: {} и {} со статусом {}", userId, friendId, statusId);
        String sql = "INSERT INTO friendships (user_id, friend_id, status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, statusId);
    }

    @Override
    public Collection<Long> getCommonFriends(Long id, Long friendId) {
        log.info("Получение общих друзей пользователей с id: {} и {}", id, friendId);
        String sql = "SELECT f1.friend_id " +
                "FROM friendships f1 " +
                "JOIN friendships f2 ON f1.friend_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, id, friendId);
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        log.info("Удаление дружбы между пользователями с id: {} и {}", userId, friendId);
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.update(sql, userId, friendId) > 0;
    }

    @Override
    public void removeFriendship(Long userId, Long friendId) {
        log.info("Удаление дружбы между пользователями с id: {} и {}", userId, friendId);
        String sql = "DELETE FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        jdbcTemplate.update(sql, userId, friendId, friendId, userId);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }
}
