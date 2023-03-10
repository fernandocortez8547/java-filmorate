package ru.yandex.practicum.filmorate.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UnknownIdExeption;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

@Service
public class InMemoryUserManager {

    private int id = 0;

    private final Map<Integer, User> users = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(InMemoryUserManager.class);
    private final Validator validator;

    public InMemoryUserManager() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public User add(User user) {
        user = userValidation(user);

        user.setId(idGeneration());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        user = userValidation(user);

        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else
            throw new UnknownIdExeption("Некорректный 'id' пользователя.");

        return user;
    }

    public List<User> getUsersList() {
        return new ArrayList<>(users.values());
    }

    private User userValidation(User user) {
        log.info("Start user validation...");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        if(!violations.isEmpty()) {
            for(ConstraintViolation<User> violation : violations) {
                throw new ValidationException(violation.getMessage());
            }
        }

        return nameValidate(user);
    }

    private User nameValidate(User user) {
        String name = user.getName();
        String login = user.getLogin();

        if(name == null || name.length() == 0) {
                int userId = user.getId();
                user = new User(user.getEmail(), login, login, user.getBirthday());

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
