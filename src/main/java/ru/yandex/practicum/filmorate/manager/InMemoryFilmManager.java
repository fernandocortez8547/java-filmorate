package ru.yandex.practicum.filmorate.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.UnknownIdExeption;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InMemoryFilmManager {

    private int id = 0;

    private final Map<Integer, Film> films = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(InMemoryUserManager.class);

    public Film add(Film film) {
        film.setId(idGeneration());
        films.put(film.getId(), film);

        return film;
    }

    public Film updateFilm(Film film) {

        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else
            throw new UnknownIdExeption("Некорректный 'id' фильма.");

        return film;
    }

    public List<Film> getFilmsList() {
        return new ArrayList<>(films.values());
    }

    private int idGeneration() {
        return ++id;
    }

}
