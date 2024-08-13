package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User addUser(User user);

    User updateUser(User user);

    Optional<User> findUserById(Long id);

    List<User> getAllUsers();

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserByLogin(String login);

    Optional<User> findUserByEmailExcludingId(String email, Long id);

    void addFriend(Long userId, Long friendId);

    List<User> getUserFriends(Long id);

    void deleteFriend(Long userId, Long friendId);

    User getUserById(Long id);

    List<User> getCommonFriends(Long userId, Long otherId);
}
