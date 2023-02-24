package ru.yandex.practicum.filmorate.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.practicum.filmorate.exception.UnknownIdExeption;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryFilmManager {

    private int id = 0;

    private final Map<Integer, Film> films = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(InMemoryUserManager.class);
    public final static int MAX_LENGTH = 199;

    public Film add(Film film) {
        if (filmValidate(film)) {
            film.setId(idGeneration());
            films.put(film.getId(), film);
        } else {
            log.info("Incorrect film fields validation");
            throw new ValidationException("Некорректные данные фильма.");
        }

        return film;
    }

    public Film updateFilm(Film film) {
        if (filmValidate(film)) {
            if (films.containsKey(film.getId())) {
                films.put(film.getId(), film);
            } else
                throw new UnknownIdExeption("Некорректный 'id' фильма.");
        } else {
            log.info("Incorrect film fields validation");
            throw new ValidationException("Некорректные данные фильма.");
        }
        return film;
    }

    public List<Film> getFilmsList() {
        return new ArrayList<>(films.values());
    }

    private boolean filmValidate(Film film) {
        log.info("Film fields validation");

        return !descriptionValidate(film.getDescription()) &&
                !nameValidate(film.getName()) &&
                !durationValidate(film.getDuration()) &&
                !releaseDateValidate(film.getReleaseDate());
    }

    private boolean descriptionValidate(String description) {
        log.info("Validate 'description' field...");

        return description.length() >= MAX_LENGTH;
    }

    private boolean nameValidate(String name) {
        log.info("Validate 'name' field...");

        return name.trim().isEmpty();
    }

    private boolean durationValidate(int duration) {
        log.info("Validate 'duration' field...");

        return duration < 0;
    }

    private boolean releaseDateValidate(LocalDate releaseDate) {
        log.info("Validate 'releaseDate' field...");

        return releaseDate.isBefore(LocalDate.of(1895, 12, 28));
    }

    private int idGeneration() {
        return ++id;
    }

}
