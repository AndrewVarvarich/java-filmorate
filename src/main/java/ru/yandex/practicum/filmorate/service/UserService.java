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

    public Optional<User> addFriend(long userId, long friendId) {
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

        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        log.info("Пользователь с id {} и пользователь с id {} теперь друзья", userId, friendId);
        return userStorage.findUserById(userId);
    }

    public void removeFriend(long userId, long friendId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        Set<Long> userFriends = user.getFriendsId();
        Set<Long> friendFriends = friend.getFriendsId();

        if (userFriends == null || !userFriends.contains(friendId) || friendFriends == null || !friendFriends.contains(userId)) {
            throw new NotFoundException("Пользователи не были друзьями");
        }

        userFriends.remove(friendId);
        friendFriends.remove(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        log.info("Пользователь с id {} и пользователь с id {} больше не друзья", userId, friendId);
    }

    public Set<User> getCommonFriends(long userId, long friendId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + friendId + " не найден"));

        Set<Long> userFriends = user.getFriendsId();
        Set<Long> friendFriends = friend.getFriendsId();

        if (userFriends == null || friendFriends == null) {
            return Collections.emptySet();
        }

        Set<Long> commonFriendsIds = new HashSet<>(userFriends);
        commonFriendsIds.retainAll(friendFriends);

        Set<User> commonFriends = new HashSet<>();
        for (Long commonFriendId : commonFriendsIds) {
            User commonFriend = userStorage.findUserById(commonFriendId)
                    .orElseThrow(() -> new NotFoundException("Друг с id " + commonFriendId + " не найден"));
            commonFriends.add(commonFriend);
        }

        return commonFriends;
    }

    public Collection<User> getFriends(long userId) {
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
