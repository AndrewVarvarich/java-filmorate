package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserStorage userStorage;


    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public Optional<User> findUserById(long id) {
        return userStorage.findUserById(id);
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public void addFriend(long userId, long friendId) {
        User user = userStorage.findUserById (userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        User friend = userStorage.findUserById (friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь с id {} добавил в друзья пользователя с id {}.", userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = userStorage.findUserById (userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (user.getFriends() == null) {
            throw new NotFoundException("У пользователя " + userId + " нет друзей");
        }

        User friend = userStorage.findUserById (friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));
        if (friend.getFriends() == null) {
            throw new NotFoundException("У пользователя с id " + friendId + " нет друзей");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь с id {} удалил их друзей пользователя с id {}.", userId, friendId);
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        Set<Long> userFriendsSet = userStorage.findUserById (userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"))
                .getFriends();
        Set<Long> otherUserFriendsSet = userStorage.findUserById (friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"))
                .getFriends();

        Set<Long> common = new HashSet<>(userFriendsSet);
        common.retainAll(otherUserFriendsSet);

        return common.stream()
                .map(id -> userStorage.findUserById (id)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден")))
                .collect(Collectors.toList());
    }

    public List<User> getFriends(long userId) {
        User user = userStorage.findUserById (userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (user.getFriends() == null) {
            throw new NotFoundException("У пользователя с id " + userId + " нет друзей");
        }
        return user.getFriends().stream()
                .map(id -> userStorage.findUserById (id)
                        .orElseThrow(() -> new NotFoundException("Пользователя с id " + id + " не существует")))
                .collect(Collectors.toList());
    }

    private User findUserOrThrow(long userId) {
        return userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}