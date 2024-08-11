package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.user.UserDbStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDbStorage userDbStorage;
    private final UserValidationService userValidation;

    public Optional<User> getUserById(Long id) {
        return userDbStorage.getUserById(id);
    }

    public User saveUser(User user) {
        userValidation.validateUser(user);
        userDbStorage.saveUser(user);
        return userDbStorage.getUserById(user.getId()).orElseThrow();
    }

    public User updateUser(Long id, User user) {
        userValidation.validateUser(user);
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

    public void addFriend(long userId, long friendId) {
        userDbStorage.addFriend(userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        userDbStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(long userId) {
        return userDbStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        return userDbStorage.getCommonFriends(userId, friendId);
    }
}
