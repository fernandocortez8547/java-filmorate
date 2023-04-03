package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
    private final GenreService genreService;

    private static final Logger log = LoggerFactory.getLogger(GenreController.class);

    @GetMapping
    public List<Genre> getGenres() {
        log.info("Request started http-method=GET http-path=/genres");
        return genreService.getGenreList();
    }

    @GetMapping("{id}")
    public Genre getGenre(@PathVariable int id) {
        log.info("Request started http-method=PUT http-path=/genres/{id}");
        return genreService.getGenre(id);
    }
}
