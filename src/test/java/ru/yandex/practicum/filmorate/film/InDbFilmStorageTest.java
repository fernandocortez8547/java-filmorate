package ru.yandex.practicum.filmorate.film;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.CreateConfiguredDb;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.InDbFilmStorage;
import ru.yandex.practicum.filmorate.storage.dao.InDbUserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@SpringBootTest
@AutoConfigureTestDatabase
public class InDbFilmStorageTest {
    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(CreateConfiguredDb.createEmbeddedDatabase());
    private final FilmStorage inDbFilmStorage = new InDbFilmStorage(jdbcTemplate);
    private final UserStorage inDbUserStorage = new InDbUserStorage(jdbcTemplate);

    private final Film film1 = Film.builder()
            .name("film1")
            .description("film1Description")
            .releaseDate(LocalDate.of(2020, 1, 1))
            .duration(110)
            .mpa(new Mpa(1, null))
            .genres(Set.of(new Genre(1, null)))
            .build();

    private final Film film2 = Film.builder()
            .name("film2")
            .description("film2Description")
            .releaseDate(LocalDate.of(2005, 12, 16))
            .duration(60)
            .mpa(new Mpa(2, null))
            .genres(Set.of(new Genre(2, null)))
            .build();

    private final Film film3 = Film.builder()
            .name("film3").description("film3Description").releaseDate(LocalDate.of(2010, 6, 6))
            .duration(40).mpa(new Mpa(3, null)).genres(Set.of(new Genre(3, null))).build();

    private final User user1 = User.builder()
            .email("user1@yandex.ru")
            .login("user1")
            .name("user1")
            .birthday(LocalDate.of(2005, 12, 16))
            .build();

    private final User user2 = User.builder()
            .email("user2@yandex.ru")
            .login("user2")
            .name("user2").birthday(LocalDate.of(2004, 11, 15))
            .build();

    private final User user3 = User.builder().email("user3@yandex.ru")
            .login("user3")
            .name("user3")
            .birthday(LocalDate.of(2003, 11, 14))
            .build();

    @AfterEach
    public void deleteExcessTable() {
        final String deleteFilmQuery = "DELETE FROM film";
        final String deleteGenreQuery = "DELETE FROM film_genres";
        final String deleteRatingQuery = "DELETE FROM film_rating";
        final String deleteLikeQuery = "DELETE FROM film_like";
        final String deleteUserQuery = "DELETE FROM \"user\"";

        jdbcTemplate.update(deleteGenreQuery);
        jdbcTemplate.update(deleteRatingQuery);
        jdbcTemplate.update(deleteLikeQuery);
        jdbcTemplate.update(deleteFilmQuery);
        jdbcTemplate.update(deleteUserQuery);
    }

    @Test
    void addFilmTest() {
        LocalDate releaseDate = LocalDate.of(2020, 1, 1);
        Mpa mpa = new Mpa(1, "G");

        Film testFilm = inDbFilmStorage.addFilm(film1);

        assertThat(testFilm).extracting("id").isNotNull();
        assertThat(testFilm).hasFieldOrPropertyWithValue("name", "film1");
        assertThat(testFilm).hasFieldOrPropertyWithValue("description", "film1Description");
        assertThat(testFilm).hasFieldOrPropertyWithValue("releaseDate", releaseDate);
        assertThat(testFilm).hasFieldOrPropertyWithValue("duration", 110);
        assertThat(testFilm).hasFieldOrPropertyWithValue("mpa", mpa);
        assertThat(testFilm).hasFieldOrPropertyWithValue("genres", Set.of(new Genre(1, "Комедия")));
    }

    @Test
    void updateFilmTest() {
        Film testFilm = inDbFilmStorage.addFilm(film1);

        film2.setId(testFilm.getId());

        testFilm = inDbFilmStorage.updateFilm(film2);

        assertThat(testFilm).extracting("id").isNotNull();
        assertThat(testFilm).hasFieldOrPropertyWithValue("name", "film2");
        assertThat(testFilm).hasFieldOrPropertyWithValue("description", "film2Description");
        assertThat(testFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2005, 12, 16));
        assertThat(testFilm).hasFieldOrPropertyWithValue("duration", 60);
        assertThat(testFilm).hasFieldOrPropertyWithValue("mpa", new Mpa(2, "PG"));
        assertThat(testFilm).hasFieldOrPropertyWithValue("genres", Set.of(new Genre(2, "Драма")));
    }

    @Test
    public void getFilmsTest() {
        inDbFilmStorage.addFilm(film1);

        assertThat(inDbFilmStorage.getFilmsList()).hasSize(1);
    }

    @Test
    public void getFilmTest() {
        int filmId = inDbFilmStorage.addFilm(film1).getId();

        assertThat(inDbFilmStorage.getFilm(filmId)).hasFieldOrPropertyWithValue("id", filmId);
    }

    @Test
    public void removeFilmTest() {
        int filmId = inDbFilmStorage.addFilm(film1).getId();
        inDbFilmStorage.removeFilm(filmId);

        assertThat(inDbFilmStorage.getFilmsList()).hasSize(0);
    }

    @Test
    public void addLike() {
        int userId = inDbUserStorage.addUser(user1).getId();
        int filmId = inDbFilmStorage.addFilm(film1).getId();

        assertThat(inDbFilmStorage.getFilmLikes(filmId)).hasSize(0);

        inDbFilmStorage.addLike(filmId, userId);

        assertThat(inDbFilmStorage.getFilmLikes(filmId)).hasSize(1);
    }

    @Test
    public void removeLike() {
        int userId = inDbUserStorage.addUser(user1).getId();
        int filmId = inDbFilmStorage.addFilm(film1).getId();

        inDbFilmStorage.addLike(filmId, userId);
        inDbFilmStorage.deleteLike(filmId, userId);

        assertThat(inDbFilmStorage.getFilmLikes(filmId)).hasSize(0);
    }

    @Test
    public void findMostPopularFilmTest() {
        int film1Id = inDbFilmStorage.addFilm(film1).getId();
        int film2Id = inDbFilmStorage.addFilm(film2).getId();
        int film3Id = inDbFilmStorage.addFilm(film3).getId();

        int user1Id = inDbUserStorage.addUser(user1).getId();
        int user2Id = inDbUserStorage.addUser(user2).getId();
        int user3Id = inDbUserStorage.addUser(user3).getId();


        inDbFilmStorage.addLike(film3Id, user1Id);
        inDbFilmStorage.addLike(film3Id, user2Id);
        inDbFilmStorage.addLike(film3Id, user3Id);

        inDbFilmStorage.addLike(film1Id, user1Id);
        inDbFilmStorage.addLike(film1Id, user2Id);

        inDbFilmStorage.addLike(film2Id, user1Id);

        List<Film> films = inDbFilmStorage.findMostPopularFilm(3);

        assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", film3Id);
        assertThat(films.get(1)).hasFieldOrPropertyWithValue("id", film1Id);
        assertThat(films.get(2)).hasFieldOrPropertyWithValue("id", film2Id);

        films = inDbFilmStorage.findMostPopularFilm(2);

        assertThat(films).hasSize(2);
    }
}
