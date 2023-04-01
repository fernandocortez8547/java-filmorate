package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.InDbGenreStorage;

import java.util.List;

@Component
public class GenreService {
    private final InDbGenreStorage genreStorage;

    public GenreService(InDbGenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenre(int id) {
        return genreStorage.getGenreById(id);
    }

    public List<Genre> getGenreList() {
        return genreStorage.getGenresList();
    }
}
