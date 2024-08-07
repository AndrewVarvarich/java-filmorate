package ru.yandex.practicum.filmorate.dao_storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    Optional<User> getUserById(Long id);
    void saveUser(User user);
    void updateUser(User user);
    boolean deleteUser(Long id);
    List<User> getAllUsers();;
}
