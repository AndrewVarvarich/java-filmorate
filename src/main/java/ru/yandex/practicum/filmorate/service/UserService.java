package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        userStorage = inMemoryUserStorage;
    }

    public Collection<User> getAllUsers() {
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
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        if (user.getFriendsId() == null) {
            user.setFriendsId(new HashSet<>());
        }

        if (friend.getFriendsId() == null) {
            friend.setFriendsId(new HashSet<>());
        }

        user.getFriendsId().add(friendId);
        friend.getFriendsId().add(userId);

        log.info("Пользователь с id {} и пользователь с id {} теперь друзья", userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        if (user.getFriendsId() == null) {
            throw new NotFoundException("У пользователя " + userId + " нет друзей");
        }

        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));
        if (friend.getFriendsId() == null) {
            throw new NotFoundException("У пользователя с id " + friendId + " нет друзей");
        }

        user.getFriendsId().remove(friendId);
        friend.getFriendsId().remove(userId);
        log.info("Пользователь с id {} и пользователь с id {} больше не друзья", userId, friendId);
    }


    public List<User> getCommonFriends(long userId, long friendId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        Set<Long> userFriends = user.getFriendsId();
        Set<Long> friendFriends = friend.getFriendsId();

        Set<Long> commonFriendsIds = new HashSet<>(userFriends);
        commonFriendsIds.retainAll(friendFriends);

        return commonFriendsIds.stream()
                .map(id -> userStorage.findUserById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден")))
                .collect(Collectors.toList());
    }

    public List<User> getFriends(long userId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Set<Long> friendsIds = user.getFriendsId();
        if (friendsIds == null || friendsIds.isEmpty()) {
            return Collections.emptyList();
        }

        return friendsIds.stream()
                .map(friendId -> userStorage.findUserById(friendId)
                        .orElseThrow(() -> new NotFoundException("Друг с id " + friendId + " не найден")))
                .collect(Collectors.toList());
    }
}
