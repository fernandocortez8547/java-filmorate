package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

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

    public List<Integer> addUsersFriends(int firstId, int secondId) {
        return userStorage.addFriendship(firstId, secondId);
    }

    public void deleteFromFriends(int id, int friendId) {
        userStorage.removeFriendship(id, friendId);
    }

    public List<User> getUserFriends(int id) {
        return userStorage.getUserFriends(id);
    }

    public List<User> getCommonFriends(int id, int otherId) {
        return userStorage.getCommonFriend(id, otherId);
    }
}
