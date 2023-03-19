package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.film.Rating;
import ru.yandex.practicum.filmorate.storage.implementation.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryFilmStorageTest {
    private Film film;
    private InMemoryFilmStorage filmManager;

    @BeforeEach
    public void createManager() {
        filmManager = new InMemoryFilmStorage();
    }

    private Film createFilm() {
        return new Film("SomeFilm",
                "SomeDescription",
                LocalDate.of(1998, 12, 16),
                90, Rating.PG);
    }

    @Test
    public void addAndUpdateFilmWithCorrectFieldsTest() {
        film = createFilm();
        Film filmFromManager = filmManager.addFilm(film);

        assertEquals(film, filmFromManager);

        Film updateFilm = new Film(film.getName(),
                "newDescription",
                film.getReleaseDate(),
                film.getDuration(),
                Rating.PG);
        updateFilm.setId(film.getId());
        filmFromManager = filmManager.updateFilm(film);

        assertEquals(film, filmFromManager);
        assertEquals(film.getId(), filmFromManager.getId());
    }


    @Test
    public void getAllFilmsTest() {
        filmManager.addFilm(createFilm());

        assertEquals(1, filmManager.getFilmsList().size());
    }
}
