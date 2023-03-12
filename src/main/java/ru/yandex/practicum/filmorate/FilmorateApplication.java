package ru.yandex.practicum.filmorate;

import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.yandex.practicum.filmorate.storage.implementation.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.implementation.InMemoryUserStorage;

@SpringBootApplication
public class FilmorateApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(FilmorateApplication.class, args);

		InMemoryFilmStorage filmStorage = context.getBean(InMemoryFilmStorage.class);
		InMemoryUserStorage userStorage = context.getBean(InMemoryUserStorage.class);
	}

}
