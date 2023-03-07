package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.implementation.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final InMemoryUserStorage userStorage = new InMemoryUserStorage();
    private UserService userService = new UserService(userStorage);

    @GetMapping
    public List<User> getUsers() {
        log.info("Request started http-method=GET http-path=/users");

        return userStorage.getUsersList();
    }

    @PostMapping
    public User addUser(@RequestBody User user, @RequestBody String string) {
        log.info("Request started http-method=POST http-path=/users");
        System.out.println(string);
        user = userStorage.addUser(user);

        log.info("Successful user add. Request finished.");
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.info("Request started http-method=PUT http-path=/users");

        user = userStorage.updateUser(user);

        log.info("Successful user update. Request finished.");
        return user;
    }

    @PostMapping("/friends")
    public void addFriendsList(@RequestBody String usersId) {
        log.info("Request started http-method=POST http-path=/users/friends");
        ObjectMapper objectMapper = new ObjectMapper();
        List<String> ids = new ArrayList<>();
        try {
            ids = objectMapper.readValue(usersId, List<java.lang.String>.class);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
        System.out.println(ids);

    }

    @GetMapping("/getArray")
    public int[] returnArray() {
        int[] someArray = {1, 2, 4};
        return someArray;
    }
}

