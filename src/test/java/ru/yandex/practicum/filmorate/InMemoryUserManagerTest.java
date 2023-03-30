package ru.yandex.practicum.filmorate;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class InMemoryUserManagerTest {

    private User user;
    private InMemoryUserStorage userManager;

    @BeforeEach
    public void createManager() {
        userManager = new InMemoryUserStorage();
    }
}
