package ru.yandex.practicum.filmorate.dao.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbc;

    public UserDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<User> getUserById(Long id) {
        String query = "SELECT * FROM \"user\" WHERE user_id = ?";
        try {
            User user = jdbc.queryForObject(query, new Object[]{id}, new UserRowMapper());
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public void saveUser(User user) {
        String query = "INSERT INTO \"user\" (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbc.update(query, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
    }

    @Override
    public void updateUser(User user) {
        String query = "UPDATE \"user\" SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbc.update(query, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
    }

    @Override
    public boolean deleteUser(Long id) {
        String query = "DELETE FROM \"user\" WHERE user_id = ?";
        int rowsDeleted = jdbc.update(query, id);
        return rowsDeleted > 0;
    }

    @Override
    public List<User> getAllUsers() {
        String query = "SELECT * FROM \"user\"";
        return jdbc.query(query, new UserRowMapper());
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String query = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)";
        jdbc.update(query, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String query = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbc.update(query, userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        String query = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM users u " +
                "JOIN friendship f ON u.id = f.friend_id " +
                "WHERE f.user_id = ?";
        return jdbc.query(query, new UserRowMapper(), userId);
    }

    @Override
    public List<User> getCommonFriends(long userId, long friendId) {
        String query = "SELECT u.id, u.email, u.login, u.name, u.birthday " +
                "FROM users u " +
                "JOIN friendship f1 ON u.id = f1.friend_id " +
                "JOIN friendship f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbc.query(query, new UserRowMapper(), userId, friendId);
    }

    public static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return User.builder()
                    .id(resultSet.getLong("user_id"))
                    .email(resultSet.getString("email"))
                    .login(resultSet.getString("login"))
                    .name(resultSet.getString("name"))
                    .birthday(resultSet.getDate("birthday") != null ?
                            resultSet.getDate("birthday").toLocalDate() : null)
                    .build();
        }
    }
}
