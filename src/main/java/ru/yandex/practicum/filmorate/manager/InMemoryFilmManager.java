package ru.yandex.practicum.filmorate.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UnknownIdExeption;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

@Service
public class InMemoryFilmManager {

    private final Validator validator;
    private int id = 0;

    private final Map<Integer, Film> films = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(InMemoryUserManager.class);

    public InMemoryFilmManager () {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    public Film add(Film film) {
        filmValidation(film);

        film.setId(idGeneration());
        films.put(film.getId(), film);

        return film;
    }

    public Film updateFilm(Film film) {
        filmValidation(film);

        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else
            throw new UnknownIdExeption("Некорректный 'id' фильма.");

        return film;
    }

    public List<Film> getFilmsList() {
        return new ArrayList<>(films.values());
    }

    private void filmValidation(Film film) {
        log.info("Start film validation...");

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        if(!violations.isEmpty()) {
            for(ConstraintViolation<Film> violation : violations) {
                throw new ValidationException(violation.getMessage());
            }
        }

    }

    private int idGeneration() {
        return ++id;
    }
}
