package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Friendship {
    private Long userId1;            // Идентификатор первого пользователя
    private Long userId2;            // Идентификатор второго пользователя
    private FriendshipStatus status; // Статус дружбы
}