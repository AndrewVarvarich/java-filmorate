package ru.yandex.practicum.filmorate.dal;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Repository
@Component("dbUserStorage")
@Primary
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    private static final String QUERY_UPDATE_USER = "UPDATE USERS SET USER_NAME = ?, EMAIL = ?, LOGIN = ?, " +
            "BIRTHDAY = ? WHERE USER_ID = ?";
    private static final String QUERY_DELETE_FRIENDSHIP = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
    private static final String QUERY_INSERT_FRIENDSHIP = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID) VALUES (?, ?)";
    private static final String QUERY_SELECT_ALL_USERS = "SELECT * FROM USERS";
    private static final String QUERY_SELECT_FRIENDS_BY_USER_ID = "SELECT FRIEND_ID AS USER_ID, EMAIL, LOGIN, " +
            "USER_NAME, BIRTHDAY FROM FRIENDSHIP INNER JOIN USERS ON FRIENDSHIP.FRIEND_ID = USERS.USER_ID WHERE " +
            "FRIENDSHIP.USER_ID = ?";
    private static final String QUERY_SELECT_FRIEND_IDS_BY_USER_ID = "SELECT FRIEND_ID FROM FRIENDSHIP WHERE " +
            "USER_ID = ?";
    private static final String QUERY_SELECT_USER_BY_ID = "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String QUERY_INSERT_USER = "INSERT INTO USERS(USER_NAME, EMAIL, LOGIN, BIRTHDAY)" +
            "VALUES (?,?,?,?)";
    private static final String QUERY_SELECT_USER_BY_EMAIL = "SELECT * FROM USERS WHERE EMAIL = ?";
    private static final String QUERY_SELECT_USER_BY_LOGIN = "SELECT * FROM USERS WHERE LOGIN = ?";
    private static final String QUERY_SELECT_USER_BY_EMAIL_EXCLUDING_ID = "SELECT * FROM USERS WHERE EMAIL = ? " +
            "AND USER_ID != ?";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User addUser(User user) {
        long id = insertWithGenId(
                QUERY_INSERT_USER,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = findMany(QUERY_SELECT_ALL_USERS);
        for (User user : users) {
            user.setFriends(getFriendsSet(user.getId()));
        }
        return users;
    }

    @Override
    public User updateUser(User updatedUser) {
        update(
                QUERY_UPDATE_USER,
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getLogin(),
                updatedUser.getBirthday(),
                updatedUser.getId()
        );
        updatedUser.setFriends(getFriendsSet(updatedUser.getId()));

        return updatedUser;
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return findOne(QUERY_SELECT_USER_BY_ID, id);
    }
    @Override
    public Optional<User> findUserByEmail(String email) {
        return findOne(QUERY_SELECT_USER_BY_EMAIL, email);
    }
    @Override
    public Optional<User> findUserByLogin(String login) {
        return findOne(QUERY_SELECT_USER_BY_LOGIN, login);
    }
    @Override
    public Optional<User> findUserByEmailExcludingId(String email, Long id) {
        return findOne(QUERY_SELECT_USER_BY_EMAIL_EXCLUDING_ID, email, id);
    }
    @Override
    public void addFriend(Long userId, Long friendId) {
        insert(QUERY_INSERT_FRIENDSHIP, userId, friendId);
    }
    @Override
    public List<User> getUserFriends(Long id) {
        List<User> friends = findMany(QUERY_SELECT_FRIENDS_BY_USER_ID, id);
        for (User friend : friends) {
            friend.setFriends(getFriendsSet(friend.getId()));
        }
        return friends;

    }
    @Override
    public void deleteFriend(Long userId, Long friendId) {
        deleteByTwoIds(QUERY_DELETE_FRIENDSHIP, userId, friendId);
    }
    @Override
    public User getUserById(Long id) {
        User user = findUserById(id).orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        user.setFriends(getFriendsSet(id));
        return user;
    }
    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        Set<Long> userFriendsSet = getUserById(userId).getFriends();
        Set<Long> otherUserFriendsSet = getUserById(otherId).getFriends();

        userFriendsSet.retainAll(otherUserFriendsSet);

        return userFriendsSet.stream()
                .map(this::getUserById)
                .collect(Collectors.toList());
    }

    private Set<Long> getFriendsSet(Long id) {
        return new HashSet<>(findManyInstances(QUERY_SELECT_FRIEND_IDS_BY_USER_ID, Long.class, id));
    }
}
