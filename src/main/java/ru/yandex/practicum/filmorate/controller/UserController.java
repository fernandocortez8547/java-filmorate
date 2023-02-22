package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private int id = 0;

    private final Map<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PutMapping
    public User addAndUpdateUser(@RequestBody User user) {
        log.info("Request started http-method=PUT http-path=/users");

        if (userValidate(user)) {
            user = nameValidate(user);

            if (users.containsValue(user)) {
                for (User u : users.values()) {
                    if (u.equals(user)) {
                        user.setId(u.getId());
                    }
                }
                users.put(user.getId(), user);
            } else {
                int id = idGeneration();
                user.setId(id);
                users.put(user.getId(), user);
            }
        } else {
            log.warn("Throw validate exception");
            throw new ValidationException("Данные пользователя введены некорректно.");
        }
        log.info("Request finished");

        return user;
    }

    private boolean userValidate(User user) {
        return !user.getEmail().isEmpty() &&
                user.getEmail() != null &&
                user.getEmail().contains("@") &&
                !user.getLogin().trim().isEmpty() &&
                !LocalDate.now().isBefore(user.getBirthay());
    }

    private User nameValidate(User user) {
        log.info("User fields validation");

        if (user.getName().length() == 0) {
            int userId = user.getId();
            user = new User(user.getEmail(), user.getLogin(), user.getLogin(), user.getBirthay());

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
