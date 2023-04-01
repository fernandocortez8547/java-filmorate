package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import ru.yandex.practicum.filmorate.annotation.NotSpace;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"id"})
@Builder(toBuilder = true)
public class User {
    private int id;
    @Email
    private final String email;
    @NotBlank
    @NotSpace
    private final String login;
    private String name;
    @Past
    private final LocalDate birthday;
}
