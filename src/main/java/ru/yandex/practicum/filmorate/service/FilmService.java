package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) { this.filmStorage = filmStorage; }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilmsList() {
        return filmStorage.getFilmsList();
    }

    public Film getFilm(int id) {
        return filmStorage.getFilm(id);
    }

    public void removeFilm(int id) {
        filmStorage.removeFilm(id);
    }

    public Film addLike(int filmId, int userId) {
        return filmStorage.addLike(filmId, userId);
    }

    public List<Integer> getFilmLikes(int film_id) {
        return filmStorage.getFilmLikes(film_id);
    }

    public Film deleteLike(int id, int userId) {
        return filmStorage.deleteLike(id, userId);
    }

    public List<Film> sortFilmsList(int count) {
        return filmStorage.findMostPopularFilm(count);
    }
}
