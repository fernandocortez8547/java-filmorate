package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.InDbGenreStorage;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreService {
    private final InDbGenreStorage genreStorage;

    public Genre getGenre(int id) {
        return genreStorage.getGenreById(id);
    }

    public List<Genre> getGenreList() {
        return genreStorage.getGenresList();
    }
}
