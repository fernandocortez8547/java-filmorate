package ru.yandex.practicum.filmorate.exception;

public class ObjectAlreadyExistException extends RuntimeException{
    public ObjectAlreadyExistException(String msg) {
        super(msg);
    }
}
