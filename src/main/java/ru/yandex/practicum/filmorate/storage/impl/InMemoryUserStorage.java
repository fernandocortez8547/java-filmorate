package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UnknownIdExeption;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private int id = 0;

    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        user = nameValidate(user);
        if(!users.containsValue(user)) {
            user.setId(idGeneration());
            users.put(user.getId(), user);
        } else
            throw new ObjectAlreadyExistException("User with email '" + user.getEmail() + "' is already exist.");

        return user;
    }

    @Override
    public User updateUser(User user) {
        user = nameValidate(user);

        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else
            throw new UnknownIdExeption("Incorrect user id: '" + user.getId() + "'.");

        return user;
    }

    @Override
    public List<User> getUsersList() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(int id) {
        if(!users.containsKey(id)) {
            throw new UnknownIdExeption("Storage don't have user with id " + id);
        }

        return users.get(id);
    }

    @Override
    public void removeUser(int id) {
        users.remove(id);
    }

    @Override
    public User addFriendship(int userId, int friendId) {
        //TODO
        return null;
    }

    @Override
    public List<User> getUserFriends(int id) {
        //TODO
        return null;
    }

    @Override
    public void removeFriendship(int userId, int friendId) {
        //TODO
    }

    @Override
    public List<User> getCommonFriend(int userId, int friendId) {
        //TODO
        return null;
    }

    private User nameValidate(User user) {
        String name = user.getName();
        String login = user.getLogin();

        if(name == null || name.length() == 0) {
                int userId = user.getId();
                user = new User(userId, user.getEmail(), login, login, user.getBirthday());

                if (userId != 0) {
                    user.setId(userId);
                }
        }

        return user;
    }


    private int idGeneration() {
        return ++id;
    }

}
