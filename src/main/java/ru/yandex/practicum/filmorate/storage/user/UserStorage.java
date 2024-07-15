package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User addUser(User user);

    User removeUser(User user);

    User updateUser(User user);

    Optional<User> findUserById(long id);

    Collection<User> getAllUsers();
}
