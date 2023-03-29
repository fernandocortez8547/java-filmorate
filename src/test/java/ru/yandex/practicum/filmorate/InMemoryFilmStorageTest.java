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

    private Film createFilm() {
        return new Film("SomeFilm",
                "SomeDescription",
                LocalDate.of(1998, 12, 16),
                90);
    }

    @Test
    public void addAndUpdateFilmWithCorrectFieldsTest() {
        film = createFilm();
        Film filmFromManager = filmManager.addFilm(film);

        assertEquals(film, filmFromManager);

        Film updateFilm = new Film(film.getName(), "newDescription", film.getReleaseDate(), film.getDuration());
        updateFilm.setId(film.getId());
        filmFromManager = filmManager.updateFilm(film);

        assertEquals(film, filmFromManager);
        assertEquals(film.getId(), filmFromManager.getId());
    }

//    @Test
//    public void addFilmWithIncorrectEmptyNameTest() {
//        film = new Film("", "someDescription", LocalDate.of(1999, 12, 1),
//                90);
//
//        assertThrows(
//                ValidationException.class,
//                () -> filmManager.addFilm(film));
//    }
//
//    @Test
//    public void addFilmWithIncorrectDescriptionLengthTest() {
//        film = new Film("someName", "tests".repeat(41), LocalDate.of(1999, 12, 1),
//                90);
//
//        assertThrows(ValidationException.class,
//                () -> filmManager.addFilm(film));
//    }
//
//    @Test
//    public void addFilmWithIncorrectReleaseDateTest() {
//        LocalDate incorrectDate = LocalDate.of(1895, 12, 17);
//
//        film = new Film("someName", "someDescription", incorrectDate, 90);
//
//        assertThrows(ValidationException.class,
//                () -> filmManager.addFilm(film));
//    }
//
//    @Test
//    public void addFilmWithIncorrectNegativeDurationTest() {
//        film = new Film("someName", "someDescription", LocalDate.of(1999, 12, 1),
//                -1);
//
//        assertThrows(ValidationException.class,
//                () -> filmManager.addFilm(film));
//    }

    @Test
    public void getAllFilmsTest() {
        filmManager.addFilm(createFilm());

        assertEquals(1, filmManager.getFilmsList().size());
    }
}
