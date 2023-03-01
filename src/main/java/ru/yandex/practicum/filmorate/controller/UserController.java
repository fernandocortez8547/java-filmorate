package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.manager.InMemoryUserManager;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final InMemoryUserManager userManager = new InMemoryUserManager();

    @GetMapping
    public List<User> getUsers() {
        log.info("Request started http-method=GET http-path=/users");

        return userManager.getUsersList();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Request started http-method=POST http-path=/users");

        user = userManager.add(user);

        log.info("Successful user add. Request finished.");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Request started http-method=PUT http-path=/users");

        user = userManager.updateUser(user);

        log.info("Successful user update. Request finished.");
        return user;
    }
}
