package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(exclude = "name")
@ToString
public class Genre {
    private int id;
    private String name;
}
