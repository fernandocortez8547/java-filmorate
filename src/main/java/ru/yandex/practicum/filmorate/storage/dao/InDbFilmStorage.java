package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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

import java.sql.*;
import java.sql.Date;
import java.util.*;


@Component
@RequiredArgsConstructor
public class InDbFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final InDbMpaStorage mpaStorage;

    @Override
    public Film addFilm(final Film film) {
        final String filmAddQuery = "INSERT INTO film " +
                "(film_name, film_description, release_date, duration, rating)" +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder id = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(filmAddQuery, new String[]{"film_id"});
            statement.setString(1, film.getName());
            statement.setString(2, film.getDescription());
            statement.setDate(3, Date.valueOf(film.getReleaseDate()));
            statement.setInt(4, film.getDuration());
            if (film.getMpa() != null) {
                statement.setInt(5, film.getMpa().getId());
            } else
                statement.setNull(5, Types.NULL);

            return statement;
        }, id);

        film.setId(Objects.requireNonNull(id.getKey()).intValue());

        if (film.getMpa() != null) {
            String name = mpaStorage.getMpaById(film.getMpa().getId()).getName();
            film.getMpa().setName(name);
        }

        if (film.getGenres() != null)
            film.setGenres(setGenres(film));
        else
            film.setGenres(Collections.emptySet());

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String filmMpaQuery = "UPDATE film SET film_name = ?," +
                " film_description = ?, release_date = ?," +
                " duration = ?, rating = ? WHERE film_id = ?";

        if (film.getGenres() != null && film.getGenres().size() != 0) {
            film.setGenres(setGenres(film));
        } else {
            film.setGenres(Collections.emptySet());
            jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        }

        int updateStatus;
        if (film.getMpa() != null) {
            film.setMpa(mpaStorage.getMpaById(film.getMpa().getId()));
            updateStatus = jdbcTemplate.update(filmMpaQuery,
                    film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(), film.getMpa().getId(),  film.getId());
        } else
            updateStatus = jdbcTemplate.update(filmMpaQuery,
                    film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(), Types.NULL, film.getId());

        if (updateStatus == 0) {
            throw new UnknownIdExeption("Storage don't have film with id " + film.getId());
        }

        return film;
    }

    @Override
    public List<Film> getFilmsList() {
        return jdbcTemplate.query("SELECT * FROM film", this::makeFilm);
    }

    @Override
    public Film getFilm(int id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM film WHERE film_id = ?", this::makeFilm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new UnknownIdExeption("Storage don't have film with id " + id);
        }
    }

    @Override
    public void removeFilm(int id) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM film_like WHERE film_id = ?", id);
        jdbcTemplate.update("DELETE FROM film WHERE film_id = ?", id);
    }

    @Override
    public Film addLike(int filmId, int userId) {
        final String likeFilmQuery = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";

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
                "FROM genre as g " +
                "LEFT JOIN film_genres as fg on g.genre_id = fg.genre_id " +
                "WHERE fg.film_id = ?";

        return new HashSet<>(jdbcTemplate.query(genresSqlQuery, this::makeGenre, filmId));
    }

    private Mpa findMpa(int filmId) {
        final String mpaSqlQuery = "SELECT r.rating_id, r.name " +
                "FROM film as f " +
                "LEFT JOIN rating as r on f.rating = r.rating_id " +
                "WHERE f.film_id = ? AND f.rating <> 0";

        try {
            return jdbcTemplate.queryForObject(mpaSqlQuery, this::makeRating, filmId);
        } catch (EmptyResultDataAccessException e ) {
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
                findMpa(filmId),
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

    private Set<Genre> setGenres(Film film) {
        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        jdbcTemplate.batchUpdate(
                "INSERT INTO film_genres (film_id, genre_id)" + "VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, film.getId());
                        ps.setInt(2, genres.get(i).getId());
                    }

                    @Override
                    public int getBatchSize() {
                        return genres.size();
                    }
                });

        return findGenres(film.getId());
    }
}

