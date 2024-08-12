package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserDbService {

    private final UserFieldsDbValidatorService userDbValidator;
    private final UserDbStorage userDbStorage;

    public List<User> getAll() {
        return userDbStorage.getAllUsers();
    }

    public User createUser(User user) {
        userDbValidator.checkUserFieldsOnCreate(user);
        return userDbStorage.addUser(user);
    }

    public User update(User updatedUser) {
        log.info("Проверка наличия id пользователя в запросе: {}.", updatedUser.getLogin());
        FieldsValidatorService.validateUserId(updatedUser);
        userDbValidator.checkUserFieldsOnUpdate(updatedUser);
        log.info("Пользователя с именем: {} обновлены.", updatedUser.getName());

        return userDbStorage.updateUser(updatedUser);
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Проверка существования пользователей: {} и {}", userId, friendId);
        findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId +
                " не найден"));
        findById(friendId).orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));
        userDbStorage.addFriend(userId, friendId);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}.", userId, friendId);
    }

    public List<User> getUserFriends(Long id) {
        findById(id).orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        return userDbStorage.getUserFriends(id);
    }

    public void deleteFriend(Long userId, Long friendId) {
        log.info("Проверка существования пользователей: {} и {}", userId, friendId);
        findById(userId).orElseThrow(() -> new NotFoundException("Пользователь с id " + userId +
                " не найден"));
        findById(friendId).orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));
        userDbStorage.deleteFriend(userId, friendId);
        log.info("Пользователь с id {} удален из друзей пользователя с id {}.", userId, friendId);
    }

    protected Optional<User> findById(Long id) {
        return userDbStorage.findUserById(id);
    }

    public User getUserById(Long id) {
        return userDbStorage.getUserById(id);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        return userDbStorage.getCommonFriends(userId, otherId);
    }
}
