package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.annotation.AfterThan;

import javax.validation.constraints.*;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(exclude = {"id"})
public class Film {
    private int id = 0;
    @NotBlank(message = "название фильма не должно быть пустым")
    private final String name;
    @Size(max=200)
    private final String description;
    @AfterThan
    private final LocalDate releaseDate;
    @Positive (message = "длительность фильма должна быть больше 0")
    private final int duration;
}
