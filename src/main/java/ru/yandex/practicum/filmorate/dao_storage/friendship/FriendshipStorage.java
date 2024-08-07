package ru.yandex.practicum.filmorate.dao_storage.friendship;

import java.util.List;

public interface FriendshipStorage {
    void addFriend(long userId, long friendId, long statusId);
    void updateFriendshipStatus(long userId, long friendId, long statusId);
    void removeFriend(long userId, long friendId);
    List<Long> getFriends(long userId);
    List<Long> getCommonFriends(long userId, long friendId);
    long getStatusIdByName(String statusName);
    long getDefaultPendingStatusId();
}