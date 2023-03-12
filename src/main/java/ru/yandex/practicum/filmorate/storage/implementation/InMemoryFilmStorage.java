package ru.yandex.practicum.filmorate.storage.implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UnknownIdExeption;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Validator validator;
    private int id = 0;

    private final Map<Integer, Film> films = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);

    public InMemoryFilmStorage() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Override
    public Film addFilm(Film film) {
        filmValidation(film);

        if (!films.containsValue(film)) {
            film.setId(idGeneration());
            films.put(film.getId(), film);
        } else
            throw new ObjectAlreadyExistException("Film with name '" + film.getName() + "' already exist.");

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        filmValidation(film);

        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else
            throw new UnknownIdExeption("Incorrect film 'id'");

        return film;
    }

    @Override
    public List<Film> getFilmsList() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(int id) {
        if (!films.containsKey(id)) {
            throw new UnknownIdExeption("Film with id " + id + " not added.");
        }

        return films.get(id);
    }

    @Override
    public void removeFilm(int id) {
        films.remove(id);
    }

    private void filmValidation(Film film) {
        log.info("Start film validation...");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        if (!violations.isEmpty()) {
            for (ConstraintViolation<Film> violation : violations) {
                throw new ValidationException(violation.getMessage());
            }
        }
    }

    private int idGeneration() {
        return ++id;
    }
}
