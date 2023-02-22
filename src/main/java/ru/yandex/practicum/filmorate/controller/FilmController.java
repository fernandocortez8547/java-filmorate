package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private int id = 0;
    public final static int MAX_LENGTH = 199;

    private final Map<Integer, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<Film>(films.values());
    }

    @PutMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Request started http-method=PUT http-path=/films");

        if(validateFilmFields(film)) {
            if (films.containsValue(film)) {
                for(Film f : films.values()) {
                    if(f.equals(film)) {
                        film.setId(f.getId());
                    }
                }
                films.put(film.getId(), film);
            } else {
                film.setId(idGeneration());
                films.put(film.getId(), film);
            }
        } else {
            log.warn("Throw validate exception");
            throw new ValidationException("Данные введены некорректно.");
        }
        log.info("Request finished");

        return film;
    }

    private boolean validateFilmFields(Film film) {
        log.info("Film fields validation");

        return film.getDescription().length() <= MAX_LENGTH &&
                !film.getName().trim().isEmpty() &&
                film.getName() != null &&
                film.getDuration() >= 0 &&
                !film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)
                );
    }

    private int idGeneration() {
        return ++id;
    }
}
