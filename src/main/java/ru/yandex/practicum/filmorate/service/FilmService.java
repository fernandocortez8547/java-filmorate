package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

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
        Film film = filmStorage.getFilm(filmId);
        User user = userStorage.getUser(userId);
        if (!film.getLikesList().contains(user.getId())) {
            film.addLike(user.getId());
        } else
            throw new ObjectAlreadyExistException
                    ("User '" + user.getId() + "' already liked film '" + film.getName() + "'");

        return film;
    }

    public Film deleteLike(int id, int userId) {
        Film film = filmStorage.getFilm(id);
        User user = userStorage.getUser(userId);

        film.removeLike(user.getId());

        return film;
    }

    public List<Film> sortFilmsList(int count) {
        return filmStorage.getFilmsList().stream()
                .sorted((o1, o2) -> o2.getLikesList().size() - o1.getLikesList().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
