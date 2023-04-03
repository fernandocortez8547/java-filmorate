package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.InDbMpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final InDbMpaStorage mpaStorage;

    public Mpa getMpa(int id) {
        return mpaStorage.getMpaById(id);
    }

    public List<Mpa> getMpaList() {
        return mpaStorage.getMpaList();
    }
}
