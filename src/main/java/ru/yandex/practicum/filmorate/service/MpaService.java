package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.InDbMpaStorage;

import java.util.List;

@Service
public class MpaService {
    private final InDbMpaStorage mpaStorage;

    public MpaService(InDbMpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Mpa getMpa(int id) {
        return mpaStorage.getMpaById(id);
    }

    public List<Mpa> getMpaList() {
        return mpaStorage.getMpaList();
    }
}
