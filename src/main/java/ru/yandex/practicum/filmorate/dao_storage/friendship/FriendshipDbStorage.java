package ru.yandex.practicum.filmorate.dao_storage.friendship;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class FriendshipDbStorage implements FriendshipStorage {

    private final JdbcTemplate jdbcTemplate;

    public FriendshipDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addFriend(long userId, long friendId, long statusId) {
        String query = "INSERT INTO friendship (user_id, friend_id, status_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(query, userId, friendId, statusId);
    }

    @Override
    public void updateFriendshipStatus(long userId, long friendId, long statusId) {
        String query = "UPDATE friendship SET status_id = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(query, statusId, userId, friendId);
    }

    @Override
    public void removeFriend(long userId, long friendId) {
        String query = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(query, userId, friendId);
    }

    @Override
    public List<Long> getFriends(long userId) {
        String query = "SELECT friend_id FROM friendship WHERE user_id = ?";
        return jdbcTemplate.queryForList(query, Long.class, userId);
    }

    @Override
    public List<Long> getCommonFriends(long userId, long friendId) {
        String query = "SELECT f1.friend_id FROM friendship f1 " +
                "JOIN friendship f2 ON f1.friend_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.queryForList(query, Long.class, userId, friendId);
    }

    @Override
    public long getStatusIdByName(String statusName) {
        String query = "SELECT status_id FROM friendship_status WHERE name = ?";
        return jdbcTemplate.queryForObject(query, Long.class, statusName);
    }

    @Override
    public long getDefaultPendingStatusId() {
        return getStatusIdByName("pending");
    }
}
