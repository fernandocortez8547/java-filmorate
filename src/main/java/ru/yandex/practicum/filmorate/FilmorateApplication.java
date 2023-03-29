package ru.yandex.practicum.filmorate;

import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(FilmorateApplication.class, args);
	}

}
