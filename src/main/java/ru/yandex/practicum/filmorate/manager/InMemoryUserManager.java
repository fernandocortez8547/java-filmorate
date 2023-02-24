package ru.yandex.practicum.filmorate.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryUserManager {

    private int id = 0;

    private final Map<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserManager.class);

    public User add(User user) {
        user = userValidate(user);
        user.setId(idGeneration());
        users.put(user.getId(), user);

        return user;
    }

    //TODO
    public User updateUser(User user) {
        user = userValidate(user);

        if (users.containsValue(user)) {
            for (User u : users.values()) {
                if (u.equals(user)) {
                    user.setId(u.getId());
                }
            }
            users.put(user.getId(), user);
        } else
            add(user);

        return user;
    }

    public List<User> getUsersList() {
        return new ArrayList<>(users.values());
    }

    private User userValidate(User user) {
        if (!(emailValidate(user.getEmail()) && loginValidate(user.getLogin()) && birthdayValidate(user.getBirthday()))) {
            log.warn("Validates failed");
            throw new ValidationException("Некорректные данные пользователя.");
        }

        if (!nameValidate(user.getName())) {
            int userId = user.getId();
            user = new User(user.getEmail(), user.getLogin(), user.getLogin(), user.getBirthday());

            if (userId != 0) {
                user.setId(userId);
            }
        }

        return user;
    }

    private boolean birthdayValidate(LocalDate birthday) {
        log.info("Validate 'birthday' field...");

        return !LocalDate.now().isBefore(birthday);
    }

    private boolean loginValidate(String login) {
        log.info("Validate 'login' field...");

        return !(login.trim().isEmpty() || login.contains(" "));
    }

    private boolean emailValidate(String email) {
        log.info("Validate 'email' field...");

        return email.contains("@");
    }

    private boolean nameValidate(String name) {
        log.info("Validate 'name' field...");

        return name.length() != 0;
    }

    private int idGeneration() {
        return ++id;
    }

}
