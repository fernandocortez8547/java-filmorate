package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UnknownIdExeption;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@Component
@RequiredArgsConstructor
public class InDbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

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

        if (film.getMpa() != null) {
            final String ratingAddQuery = "INSERT INTO film_rating (film_id, rating_id)" + "VALUES (?, ?)";
            jdbcTemplate.update(ratingAddQuery, film.getId(), film.getMpa().getId());
            film.setMpa(findRating(film.getId()));
        }

        if (film.getGenres() != null) {
            final String genresAddQuery = "INSERT INTO film_genres (film_id, genre_id)" + "VALUES (?, ?)";
            for (Genre g : film.getGenres()) {
                jdbcTemplate.update(genresAddQuery, film.getId(), g.getId());
            }
            film.setGenres(findGenres(film.getId()));
        } else
            film.setGenres(Collections.emptySet());

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String dbContainsQuery = "SELECT * FROM film WHERE film_id = ?";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(dbContainsQuery, film.getId());
        if (!rowSet.next()) {
            throw new UnknownIdExeption("Storage don't have film with id " + film.getId());
        }

        String updateFilmQuery = "UPDATE film SET film_name = ?," +
                " film_description = ?, release_date = ?," +
                " duration = ? WHERE film_id = ?";

        if (film.getMpa() != null) {
            String deleteOldRatingQuery = "DELETE FROM film_rating WHERE film_id = ?";
            String setRatingQuery = "INSERT INTO film_rating (film_id, rating_id)" + "VALUES (?, ?)";

            jdbcTemplate.update(deleteOldRatingQuery, film.getId());
            jdbcTemplate.update(setRatingQuery, film.getId(), film.getMpa().getId());
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

        if (film.getGenres() != null) {
            film.setGenres(findGenres(film.getId()));
        } else {
            film.setGenres(Collections.emptySet());
        }

        film.setMpa(findRating(film.getId()));

        return film;
    }

    @Override
    public List<Film> getFilmsList() {
        return jdbcTemplate.query("SELECT * FROM film", this::makeFilm);
    }

    @Override
    public Film getFilm(int id) {
        final String sqlQuery = "SELECT * FROM film WHERE film_id = ?";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, id);

        if(!rowSet.next()) {
            throw new UnknownIdExeption("Storage don't have film with id " + id);
        }

        return jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
    }

    @Override
    public void removeFilm(int id) {
        jdbcTemplate.update("DELETE FROM film_rating WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM film_like WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM film WHERE film_id = ?", id);
    }

    @Override
    public Film addLike(int filmId, int userId) {
        final String likeFilmQuery = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";

        validate(filmId, userId);
        validateLike(filmId, userId);
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

    @Override
    public List<Integer> getFilmLikes(int filmId) {
        final String userLikesQuery = "SELECT user_id FROM film_like WHERE film_id = ?";

        return jdbcTemplate.query(userLikesQuery, (rs, rn) -> rs.getInt("user_id"), filmId);
    }

    @Override
    public List<Film> findMostPopularFilm(int limit) {
        final String mostLikeFilmQuery = "SELECT f.*, fl.cf FROM film AS f " +
                "LEFT JOIN " +
                "(SELECT film_id, COUNT(user_id) as cf FROM film_like GROUP BY film_id ORDER BY COUNT(user_id)) " +
                "AS fl ON f.film_id = fl.film_id " +
                "ORDER BY fl.cf DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(mostLikeFilmQuery, this::makeFilm, limit);
    }

    private void validate(int filmId, int userId) {
        final String filmValidateQuery = "SELECT * FROM film WHERE film_id = ?";
        final String userValidateQuery = "SELECT * FROM \"user\" WHERE user_id = ?";

        SqlRowSet filmRowSet = jdbcTemplate.queryForRowSet(filmValidateQuery, filmId);
        SqlRowSet userRowSet = jdbcTemplate.queryForRowSet(userValidateQuery, userId);

        if (!filmRowSet.next() || !userRowSet.next()) {
            throw new UnknownIdExeption("Storage don't have user or film with this id's.");
        }
    }

    private void validateLike(int filmId, int userId) {
        final String sqlQuery = "SELECT * FROM film_like WHERE film_id = ? AND user_id = ?";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sqlQuery, filmId, userId);

        if (rowSet.next()) {
            throw new ObjectAlreadyExistException("User like is already exist for film with id " + filmId);
        }
    }

    private Set<Genre> findGenres(int filmId) {
        final String genresSqlQuery = "SELECT fg.genre_id, name " +
                "FROM genre " +
                "LEFT JOIN film_genres as fg on genre.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(genresSqlQuery, filmId);

        if (rowSet.next())
            return new HashSet<>(jdbcTemplate.query(genresSqlQuery, this::makeGenre, filmId));
        else
            return Collections.emptySet();
    }

    private Mpa findRating(int filmId) {
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

    private Mpa makeRating(ResultSet rs, int rn) throws SQLException {
        return new Mpa(
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
