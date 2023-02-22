package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(exclude = {"id"})
public class User {
    private int id = 0;
    private final String email;
    private final String login;
    private final String name;
    private final LocalDate birthay;
}
