package ru.yandex.practicum.filmorate.exception;

public class UnknownIdExeption extends RuntimeException {
    public UnknownIdExeption(String msg) {
        super(msg);
    }
}
