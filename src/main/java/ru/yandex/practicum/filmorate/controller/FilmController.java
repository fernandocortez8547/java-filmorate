package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.manager.InMemoryFilmManager;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final InMemoryFilmManager filmManager = new InMemoryFilmManager();

    @GetMapping
    public List<Film> getFilms() {
        log.info("Request started http-method=GET http-path=/films");

        return filmManager.getFilmsList();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Request started http-method=POST http-path=/films");

        film = filmManager.add(film);

        log.info("Successful film add. Request finished.");
        return film;
    }

    @PutMapping
    public Film addAndUpdateFilm(@RequestBody Film film) {
        log.info("Request started http-method=PUT http-path=/films");

        film = filmManager.updateFilm(film);

        log.info("Successful film update. Request finished.");
        return film;
    }
}
