package ru.yandex.practicum.filmorate.storage.implementation;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UnknownIdExeption;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private int id = 0;

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        if (!films.containsValue(film)) {
            film.setId(idGeneration());
            films.put(film.getId(), film);
        } else
            throw new ObjectAlreadyExistException("Film with name '" + film.getName() + "' already exist.");

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
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
            throw new UnknownIdExeption("Film with id '" + id + "' not added.");
        }

        return films.get(id);
    }

    @Override
    public void removeFilm(int id) {
        films.remove(id);
    }

    private int idGeneration() {
        return ++id;
    }
}
