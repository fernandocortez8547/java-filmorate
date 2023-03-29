package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UnknownIdExeption;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Primary
@Component("inDBFilmStorage")
@RequiredArgsConstructor
public class InDbFilmStorage implements FilmStorage {

    @Autowired
    private final JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(InDbFilmStorage.class);

    @Override
    public Film addFilm(Film film) {
        final String filmAddQuery = "INSERT INTO film (film_name, film_description, release_date, duration)" +
                "VALUES (?, ?, ?, ?)";
        KeyHolder id = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(filmAddQuery, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());

            return statement;
        }, id);

        film.setId(Objects.requireNonNull(id.getKey()).intValue());


        final String ratingAddQuery = "INSERT INTO film_rating (film_id, rating_id)" + "VALUES (?, ?)";
        jdbcTemplate.update(ratingAddQuery, film.getId(), film.getRating().getRatingId());
        film.setRating(findRating(film.getId()));

        final String genresAddQuery = "INSERT INTO film_genres (film_id, genre_id)" + "VALUES (?, ?)";
        for (Genre g : film.getGenres()) {
            jdbcTemplate.update(genresAddQuery, film.getId(), g.getId());
        }
        film.setGenres(findGenres(film.getId()));

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String dbContainsQuery = "SELECT * FROM film WHERE film_id = ?";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(dbContainsQuery, film.getId());
        if (!rowSet.next()) {
            throw new UnknownIdExeption("Storage don't have user with id " + film.getId());
        }

        String updateFilmQuery = "UPDATE film SET film_name = ?," +
                " film_description = ?, release_date = ?," +
                " duration = ? WHERE film_id = ?";

        if (film.getRating() != null) {
            String deleteOldRatingQuery = "DELETE FROM film_rating WHERE film_id = ?";
            String setRatingQuery = "INSERT INTO film_rating (film_id, rating_id)" + "VALUES (?, ?)";

            jdbcTemplate.update(deleteOldRatingQuery, film.getId());
            jdbcTemplate.update(setRatingQuery, film.getId(), film.getRating().getRatingId());
        }

        if (film.getGenres() != null) {
            String deleteOldRatingQuery = "DELETE FROM film_genres WHERE film_id = ?";
            String setRatingQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";

            jdbcTemplate.update(deleteOldRatingQuery, film.getId());
            for (Genre g : film.getGenres()) {
                jdbcTemplate.update(setRatingQuery, film.getId(), g.getId());
            }
        }

        jdbcTemplate.update(updateFilmQuery, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getId());

        film.setGenres(findGenres(film.getId()));
        film.setRating(findRating(film.getId()));

        return film;
    }

    @Override
    public List<Film> getFilmsList() {
        return jdbcTemplate.query("SELECT * FROM film", this::makeFilm);
    }

    @Override
    public Film getFilm(int id) {
        return jdbcTemplate.queryForObject("SELECT * FROM film WHERE film_id = ?", this::makeFilm, id);
    }

    @Override
    public void removeFilm(int id) {
        jdbcTemplate.update("DELETE FROM film_rating WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM film WHERE film_id = ?", id);
    }

    @Override
    public Film addLike(int filmId, int userId) {
        final String likeFilmQuery = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";

        validate(filmId, userId);
        jdbcTemplate.update(likeFilmQuery, filmId, userId);

        return getFilm(filmId);
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        final String deleteLikeFilmQuery = "DELETE FROM film_like WHERE film_id = ? AND user_id = ?";

        validate(filmId, userId);
        jdbcTemplate.update(deleteLikeFilmQuery, filmId, userId);

        return getFilm(filmId);
    }

    private void validate(int filmId, int userId) {
        final String filmValidateQuery = "SELECT * FROM film WHERE film_id = ?";
        final String userValidateQuery = "SELECT * FROM user WHERE user_id = ?";

        SqlRowSet filmRowSet = jdbcTemplate.queryForRowSet(filmValidateQuery, filmId);
        SqlRowSet userRowSet = jdbcTemplate.queryForRowSet(userValidateQuery, userId);

        if (!filmRowSet.next() || !userRowSet.next()) {
            throw new UnknownIdExeption("Storage don't have user or film with this id's.");
        }
    }

    private List<Genre> findGenres(int filmId) {
        final String genresSqlQuery = "SELECT fg.genre_id, name " +
                "FROM genre " +
                "LEFT JOIN film_genres as fg on genre.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(genresSqlQuery, filmId);

        if (rowSet.next()) {
            return jdbcTemplate.query(genresSqlQuery, this::makeGenre, filmId);
        } else {
            return null;
        }
    }

    private Rating findRating(int filmId) {
        final String genresSqlQuery = "SELECT fr.rating_id, name " +
                "FROM rating " +
                "LEFT JOIN film_rating as fr on rating.rating_id = fr.rating_id " +
                "WHERE fr.film_id = ?";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(genresSqlQuery, filmId);

        if (rowSet.next()) {
            return jdbcTemplate.queryForObject(genresSqlQuery, this::makeRating, filmId);
        } else {
            return null;
        }
    }

    private Film makeFilm(ResultSet rs, int rn) throws SQLException {
        int filmId = rs.getInt("film_id");

        return new Film(
                filmId,
                rs.getString("film_name"),
                rs.getString("film_description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                findRating(filmId),
                findGenres(filmId)
        );
    }

    private Rating makeRating(ResultSet rs, int rn) throws SQLException {
        return new Rating(
                rs.getInt("rating_id"),
                rs.getString("name")
        );
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")
        );
    }
}
