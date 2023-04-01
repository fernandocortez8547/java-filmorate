package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.dao.InDbMpaStorage;

import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    Logger log = LoggerFactory.getLogger(MpaController.class);

    @GetMapping
    public List<Mpa> getRatings() {
        log.info("Request started http-method=GET http-path=/mpa");
        return mpaService.getMpaList();
    }

    @GetMapping("{id}")
    public Mpa getRating(@PathVariable int id) {
        log.info("Request started http-method=PUT http-path=/mpa/{id}");
        return mpaService.getMpa(id);
    }
}
