package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class FilmService {
    @Autowired
    private FilmStorage filmStorage;
    @Autowired
    private UserStorage userStorage;

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

    public Film deleteLike(int id, int userId) {
        return filmStorage.deleteLike(id, userId);
    }
//
//    public List<Film> sortFilmsList(int count) {
//        return filmStorage.getFilmsList().stream()
//                .sorted((o1, o2) -> o2.getLikesList().size() - o1.getLikesList().size())
//                .limit(count)
//                .collect(Collectors.toList());
//    }
}
