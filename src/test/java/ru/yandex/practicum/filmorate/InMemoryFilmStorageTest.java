package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryFilmStorageTest {
    private Film film;
    private InMemoryFilmStorage filmManager;

    @BeforeEach
    public void createManager() {
        filmManager = new InMemoryFilmStorage();
    }
}
