package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.annotation.NotSpace;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(exclude = {"id"})
public class User {
    private int id = 0;
    @Email
    private final String email;
    @NotBlank
    @NotSpace
    private final String login;
    private final String name;
    @Past
    private final LocalDate birthday;

    private List<Integer> friendsList = new ArrayList<>();

    public void addFriend(int id) {
        friendsList.add(id);
    }

    public void removeFriend(Integer id) {
        friendsList.remove(id);
    }
}
