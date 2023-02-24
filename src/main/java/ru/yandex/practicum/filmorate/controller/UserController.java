package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.manager.InMemoryUserManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private int id = 0;

    private final Map<Integer, User> users = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final InMemoryUserManager userManager = new InMemoryUserManager();

    @GetMapping
    public List<User> getUsers() {
         return userManager.getUsersList();
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        log.info("Request started http-method=POST http-path=/users");

        if(!userManager.getUsersList().contains(user)) {
            user = userManager.add(user);
            log.info("Successful user add");
        } else
            log.warn("Incorrect http-method for update user fields");

        log.info("Request finished.");

        return user;
    }

    @PutMapping
    public User addAndUpdateUser(@RequestBody User user) {
        log.info("Request started http-method=PUT http-path=/users");

        if(userManager.getUsersList().contains(user)) {
            user = userManager.updateUser(user);
            log.info("Successful user add");
        } else {
            user = userManager.add(user);
            log.info("Successful user update");
        }

        log.info("Request finished.");

        return user;
    }
}
