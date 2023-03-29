package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.*;

@CrossOrigin
@RestController
@RequestMapping("/films")
public class FilmController {
    @Autowired
    private FilmService filmService;

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Request started http-method=POST http-path=/films");

        film = filmService.addFilm(film);

        log.info("Successful film add. Request finished.");
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Request started http-method=PUT http-path=/films");

        film = filmService.updateFilm(film);

        log.info("Successful film update. Request finished.");
        return film;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Request started http-method=GET http-path=/films");

        return filmService.getFilmsList();
    }

    @GetMapping("{id}")
    public Film getFilm(@PathVariable Integer id) {
        log.info("Request started http-method=GET http-path=/films/{id}");

        return filmService.getFilm(id);
    }

    @DeleteMapping("{id}")
    public void deleteFilm(@PathVariable Integer id) {
        log.info("Request started http-method=DELETE http-path=/films/{id}");

        filmService.removeFilm(id);
    }

    @PutMapping("{id}/like/{userId}")
    public Film likeFilm(@PathVariable int id, @PathVariable int userId) {
        log.info("Request started http-method=PUT http-path=/films/{id}/like/{userId}");

        Film film = filmService.addLike(id, userId);

        log.info("Successful add like. Request finished.");
        return film;
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Request started http-method=DELETE http-path=/films/{id}/like/{userId}");

        Film film = filmService.deleteLike(id, userId);

        log.info("Successful delete like. Request finished.");
        return film;
    }
//
//    @GetMapping("/popular")
//    public List<Film> getMostPopularFilms(@RequestParam(defaultValue = "10") int count) {
//        log.info("Request started http-method=GET http-path=/films/popular?count={count}");
//
//        return filmService.sortFilmsList(count);
//    }
}

