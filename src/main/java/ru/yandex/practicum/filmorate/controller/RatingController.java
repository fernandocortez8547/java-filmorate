package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.impl.InDbRatingStorage;

import java.util.Collection;


@RestController
@RequestMapping("/rating")
public class RatingController {
    @Autowired
    InDbRatingStorage ratingStorage;

    Logger log = LoggerFactory.getLogger(RatingController.class);

    @GetMapping
    public Collection<Rating> getRatings() {
        log.info("Request started http-method=GET http-path=/rating");
        return ratingStorage.getAllRatings();
    }

    @GetMapping("{id}")
    public Rating getRating(@PathVariable int id) {
        log.info("Request started http-method=PUT http-path=/rating/{id}");
        return ratingStorage.getRatingById(id);
    }
}
