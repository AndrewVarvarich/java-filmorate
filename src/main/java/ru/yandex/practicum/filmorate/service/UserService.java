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
        User user = findUserOrThrow(userId);
        User friend = findUserOrThrow(friendId);

        if (user.getFriendsId() == null) {
            user.setFriendsId(new HashSet<>());
        }

        if (friend.getFriendsId() == null) {
            friend.setFriendsId(new HashSet<>());
        }

        user.getFriendsId().add(friendId);
        friend.getFriendsId().add(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        log.info("Пользователь с id {} и пользователь с id {} теперь друзья", userId, friendId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = findUserOrThrow(userId);
        User friend = findUserOrThrow(friendId);

        if (user.getFriendsId() != null && user.getFriendsId().contains(friendId)) {
            user.getFriendsId().remove(friendId);
        }

        if (friend.getFriendsId() != null && friend.getFriendsId().contains(userId)) {
            friend.getFriendsId().remove(userId);
        }

        log.info("Пользователь с id {} и пользователь с id {} больше не друзья", userId, friendId);
    }

    public List<User> getCommonFriends(long userId, long friendId) {
        User user = findUserOrThrow(userId);
        User friend = findUserOrThrow(friendId);

        Set<Long> userFriends = user.getFriendsId();
        Set<Long> friendFriends = friend.getFriendsId();

        Set<Long> commonFriendsIds = new HashSet<>(userFriends);
        commonFriendsIds.retainAll(friendFriends);

        return commonFriendsIds.stream()
                .map(id -> userStorage.findUserById(id)
                        .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден")))
                .collect(Collectors.toList());
    }

    public Collection<User> getFriends(long userId) {
        User user = findUserOrThrow(userId);

        Set<Long> friendsIds = user.getFriendsId();
        if (friendsIds == null || friendsIds.isEmpty()) {
            return Collections.emptyList();
        }

        return friendsIds.stream()
                .map(friendId -> findUserOrThrow(friendId))
                .collect(Collectors.toList());
    }

    private User findUserOrThrow(long userId) {
        return userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}
