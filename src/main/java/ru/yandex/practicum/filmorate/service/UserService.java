package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Service
public class UserService {
    private UserStorage storage;

    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public User addFriends(User user) {
        if(!user.getFriendsList().isEmpty()) {
            for(int id : user.getFriendsList()) {
                User currentFriend = storage.getUser(id);
                user.addFriendsId(id);
                currentFriend.addFriendsId(user.getId());
            }
        }

        return user;
    }
}
