package ru.yandex.practicum.filmorate.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.exception.UnknownIdExeption;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryUserManager {

    private int id = 0;

    private final Map<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserManager.class);

    public User add(User user) {
        user = nameValidate(user);
        user.setId(idGeneration());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        user = nameValidate(user);

        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else
            throw new UnknownIdExeption("Некорректный 'id' пользователя.");

        return user;
    }

    public List<User> getUsersList() {
        return new ArrayList<>(users.values());
    }

    private User nameValidate(User user) {
        if (!nameValidate(user.getName())) {
            int userId = user.getId();
            user = new User(user.getEmail(), user.getLogin(), user.getLogin(), user.getBirthday());

            if (userId != 0) {
                user.setId(userId);
            }
        }

        return user;
    }

    private boolean nameValidate(String name) {
        log.info("Validate 'name' field...");

        if (name == null)
            return false;

        return name.length() != 0;
    }

    private int idGeneration() {
        return ++id;
    }

}
