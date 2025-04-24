package ru.yandex.practicum.filmorate.storage.dao.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FriendStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendStatusService;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Component
@Qualifier("SQL_User_Storage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendStatusService friendStatusService;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendStatusService friendStatusService) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendStatusService = friendStatusService;
    }

    @Override
    public Collection<Long> findAllKeys() {
        String sql = "SELECT id FROM users";
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    @Override
    public Collection<Long> findAllFriends(Long id) {
        String sql = "SELECT friend_id FROM friendships WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, id);
    }

    @Override
    public Collection<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::mapRowToUser);
    }

    @Override
    public User getUser(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, id);
        if (users.isEmpty()) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return users.get(0);
    }

    @Override
    public User create(User user) {
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
        return user;
    }

    @Override
    public User update(User user) {
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
        return user;
    }

    @Override
    public long addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO friendships (user_id, friend_id, status_id) VALUES (?, ?, ?)";
        FriendStatus status = friendStatusService.get(1L).orElseThrow(() -> new NotFoundException("Статус дружбы не найден"));
        jdbcTemplate.update(sql, userId, friendId, status.getId());
        return friendId;
    }

    @Override
    public Collection<Long> getCommonFriends(Long id, Long friendId) {
        String sql = "SELECT f1.friend_id " +
                "FROM friendships f1 " +
                "JOIN friendships f2 ON f1.friend_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, id, friendId);
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";
        return jdbcTemplate.update(sql, userId, friendId) > 0;
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

