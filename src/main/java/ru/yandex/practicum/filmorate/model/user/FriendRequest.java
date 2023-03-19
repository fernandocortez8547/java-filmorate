package ru.yandex.practicum.filmorate.model.user;

import lombok.Data;

@Data
public class FriendRequest {
    private final int friendId;
    private final boolean requestStatus;
}
