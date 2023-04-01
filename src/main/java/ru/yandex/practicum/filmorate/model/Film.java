package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.annotation.AfterThan;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@EqualsAndHashCode(exclude = {"id"})
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
public class Film {
    private int id;
    @NotBlank(message = "название фильма не должно быть пустым")
    private final String name;
    @Size(max = 200)
    private final String description;
    @AfterThan
    private final LocalDate releaseDate;
    @Positive (message = "длительность фильма должна быть больше 0")
    private final int duration;
    private Mpa mpa;
    private Set<Genre> genres;
}
