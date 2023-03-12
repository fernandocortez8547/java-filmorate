package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage storage) {
        this.userStorage = storage;
    }

    public User addUser(User user) {
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public List<User> getUsersList() {
        return userStorage.getUsersList();
    }

    public User getUser(int id) {
        return userStorage.getUser(id);
    }

    public void removeUser(int id) {
        userStorage.removeUser(id);
    }

    public List<User> addUsersFriends(int firstId, int secondId) {
        User user1 = userStorage.getUser(firstId);
        User user2 = userStorage.getUser(secondId);

        if(!user1.getFriendsList().contains(user2.getId())) {
            user1.addFriend(user2.getId());
            user2.addFriend(user1.getId());
        } else
            throw new ObjectAlreadyExistException("Users [" + firstId + "], [" + secondId + "] already friends.");

        return List.of(user1, user2);
    }

    public List<User> removeUsersFromFriend(int id, int friendId) {
        User user1 = userStorage.getUser(id);
        User user2 = userStorage.getUser(friendId);

        user1.removeFriend(friendId);
        user2.removeFriend(id);

        return List.of(user1, user2);
    }

    public List<User> getUserFriends(int id) {
        User user = userStorage.getUser(id);

        return user.getFriendsList().stream()
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int otherId) {
        User user = userStorage.getUser(id);
        User otherUser = userStorage.getUser(otherId);

        return user.getFriendsList().stream()
                .filter(t -> otherUser.getFriendsList().contains(t))
                .map(userStorage::getUser)
                .collect(Collectors.toList());
    }
}
