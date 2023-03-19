package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.user.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Request started http-method=POST http-path=/users");
        user = userService.addUser(user);

        log.info("Successful user add. Request finished.");
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Request started http-method=PUT http-path=/users");

        user = userService.updateUser(user);

        log.info("Successful user update. Request finished.");
        return user;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Request started http-method=GET http-path=/users");

        return userService.getUsersList();
    }

    @GetMapping("{id}")
    public User getUser(@PathVariable int id) {
        log.info("Request started http-method=GET http-path=/users/{id}");

        return userService.getUser(id);
    }

    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable int id) {
        log.info("Request started http-method=GET http-path=/users/{id}");

        userService.removeUser(id);
    }

    @PutMapping("{id}/friends/{friendId}")
    public List<User> addToFriends(@PathVariable int id, @PathVariable int friendId) {
        log.info("Request started http-method=POST http-path=/users/{id}/friends{friendId}");

        List<User> users = userService.addUsersFriends(id, friendId);

        log.info("Successful add friend. Request finished.");
        return users;
    }

    @GetMapping("{id}/friends")
    public List<User> getUserFriends(@PathVariable int id) {
        log.info("Request started http-method=POST http-path=/users/{id}/friends");

        return userService.getUserFriends(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public List<User> deleteFromFriends(@PathVariable int id, @PathVariable int friendId) {
        log.info("Request started http-method=DELETE http-path=/users/{id}/friends{friendId}");

        List<User> users = userService.removeUsersFromFriend(id, friendId);

        log.info("Successful friend delete. Request finished.");
        return users;
    }
}

