package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(exclude = {"id"})
public class Film {
    private int id = 0;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final int duration;
}
