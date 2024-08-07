package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao_storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserService(UserDbStorage userDbStorage, JdbcTemplate jdbcTemplate) {
        this.userDbStorage = userDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<User> getUserById(Long id) {
        return userDbStorage.getUserById(id);
    }

    public User saveUser(User user) {
        userDbStorage.saveUser(user);
        return userDbStorage.getUserById(user.getId()).orElseThrow();
    }

    public User updateUser(Long id, User user) {
        if (!id.equals(user.getId())) {
            throw new IllegalArgumentException("Id не совпадают");
        }
        userDbStorage.updateUser(user);
        return userDbStorage.getUserById(id).orElseThrow(() -> new RuntimeException("Пользователь не найден после" +
                "обновления"));
    }

    public boolean deleteUser(Long id) {
        return userDbStorage.deleteUser(id);
    }

    public List<User> getAllUsers() {
        return userDbStorage.getAllUsers();
    }

    public void addFriend(long userId, long friendId, String statusName) {
        long statusId = getStatusIdByName(statusName);
        String query = "INSERT INTO friendship (user_id, friend_id, status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(query, userId, friendId, statusId);
    }

    public void updateFriendshipStatus(long userId, long friendId, String statusName) {
        long statusId = getStatusIdByName(statusName);
        String query = "UPDATE friendship SET status_id = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(query, statusId, userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        String query = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(query, userId, friendId);
    }

    public List<Long> getFriends(long userId) {
        String query = "SELECT friend_id FROM friendship WHERE user_id = ?";
        return jdbcTemplate.queryForList(query, Long.class, userId);
    }

    public List<Long> getCommonFriends(long userId, long friendId) {
        String query = "SELECT f1.friend_id FROM friendship f1 " +
                "JOIN friendship f2 ON f1.friend_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.queryForList(query, Long.class, userId, friendId);
    }

    private long getStatusIdByName(String statusName) {
        String query = "SELECT status_id FROM friendship_status WHERE name = ?";
        return jdbcTemplate.queryForObject(query, Long.class, statusName);
    }
}
