package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component("InMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        user.setNameIfEmpty();
        user.setId(getNextUserId());
        users.put(user.getId(), user);
        log.info("Объект успешно добавлен\n", user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        if (newUser.getId() == null) {
            log.error("Ошибка в id\n");
            throw new ValidationException("Id должен быть указан");
        }
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            newUser.setNameIfEmpty();
            User updateUser = User.builder()
                    .id(oldUser.getId())
                    .email(newUser.getEmail())
                    .login(newUser.getLogin())
                    .name(newUser.getName())
                    .birthday(newUser.getBirthday())
                    .friends(newUser.getFriends())
                    .build();
            users.put(updateUser.getId(), updateUser);
            log.info("Объект успешно обновлен\n", updateUser);
            return updateUser;
        }
        log.info("Объект не добавлен\n");
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findUserByLogin(String login) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findUserByEmailExcludingId(String email, Long id) {
        return Optional.empty();
    }

    @Override
    public void addFriend(Long userId, Long friendId) {

    }

    @Override
    public List<User> getUserFriends(Long id) {
        return null;
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {

    }

    @Override
    public User getUserById(Long id) {
        return null;
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        return null;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    private long getNextUserId() {
        return users.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }
}
