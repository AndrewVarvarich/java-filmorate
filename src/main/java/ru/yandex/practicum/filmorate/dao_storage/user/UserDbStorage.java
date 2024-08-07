package ru.yandex.practicum.filmorate.dao_storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
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

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
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

    public static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
            return User.builder()
                    .id(resultSet.getLong("user_id"))
                    .email(resultSet.getString("email"))
                    .login(resultSet.getString("login"))
                    .name(resultSet.getString("name"))
                    .birthday(resultSet.getDate("birthday") != null ? resultSet.getDate("birthday").toLocalDate() : null)
                    .build();
        }
    }
}
