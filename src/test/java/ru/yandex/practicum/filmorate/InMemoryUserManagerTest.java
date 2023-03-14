package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.storage.implementation.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class InMemoryUserManagerTest {

    private User user;
    private InMemoryUserStorage userManager;

    @BeforeEach
    public void createManager() {
        userManager = new InMemoryUserStorage();
    }

    private User createUser() {
        return new User("Email@With.At",
                "someLogin",
                "someName",
                LocalDate.of(1998, 12, 16));
    }

    @Test
    public void addAndUpdateUserWithCorrectUserFieldsTest() {
        user = createUser();
        User userFromManager = userManager.addUser(user);

        assertEquals(user, userFromManager);

        User updateUser = new User(user.getEmail(), "newLogin", user.getName(), user.getBirthday());
        updateUser.setId(user.getId());
        userFromManager = userManager.updateUser(user);

        assertEquals(user, userFromManager);
        assertEquals(user.getId(), userFromManager.getId());
    }

//    @Test
//    public void addUserWithIncorrectEmailWithoutAtTest() {
//        user = new User("EmailWithoutAt",
//                "someLogin",
//                "someName",
//                LocalDate.of(1998, 12, 16));
//
//        assertThrows(
//                ValidationException.class,
//                () -> userManager.addUser(user)
//        );
//    }
//
//    @Test
//    public void addUserWithIncorrectEmptyLoginTest() {
//        user = new User("Email@With.At",
//                "",
//                "someName",
//                LocalDate.of(1998, 12, 16));
//
//        assertThrows(
//                ValidationException.class,
//                () -> userManager.addUser(user)
//        );
//    }
//
//    @Test
//    public void addUserWithIncorrectLoginWithWhitespaceTest() {
//        user = new User("Email@With.At",
//                "some Login",
//                "someName",
//                LocalDate.of(1998, 12, 16));
//
//        assertThrows(
//                ValidationException.class,
//                () -> userManager.addUser(user)
//        );
//    }
//
//    @Test
//    public void addUserWithIncorrectBirthdayDateTest() {
//        user = new User("Email@With.At",
//                "someLogin",
//                "someName",
//                LocalDate.now().plusDays(1));
//
//        assertThrows(
//                ValidationException.class,
//                () -> userManager.addUser(user)
//        );
//    }
//
    @Test
    public void addUserWithEmptyNameTest() {
        user = new User("Email@With.At",
                "someLogin",
                "",
                LocalDate.of(1998, 12, 16)
        );

        assertEquals(user.getLogin(), userManager.addUser(user).getName());
    }

    @Test
    public void getAllUserTest() {
        user = createUser();
        userManager.addUser(user);

        assertEquals(1, userManager.getUsersList().size());
    }
}
