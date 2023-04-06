package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
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

    @Override
    public Film addFilm(final Film film) {
        final String filmAddQuery = "INSERT INTO film " +
                "(film_name, film_description, release_date, duration, rating) " +
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

        int updateStatus = jdbcTemplate.update(filmMpaQuery,
                film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(), film.getId());

        if (updateStatus == 0) {
            throw new UnknownIdExeption("Storage don't have film with id " + film.getId());
        }

        return film;
    }

    @Override
    public List<Film> getFilmsList() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM film as f JOIN rating as r ON f.rating = r.rating_id",
                this::makeFilm);

        return connectingAllFilmGenre(films);
    }

    @Override
    public Film getFilm(int id) {
        try {
            Film film = jdbcTemplate.queryForObject("SELECT * FROM film as f " +
                            "JOIN rating as r ON f.rating = r.rating_id WHERE film_id = ?",
                    this::makeFilm, id);

            assert film != null;
            film.setGenres(getSpecificGenres(id));

            return film;
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
        final String mostLikeFilmQuery = "SELECT f.*, fl.cf FROM " +
                "(SELECT film.*, r.name FROM film JOIN rating as r ON film.rating = r.rating_id) AS f " +
                "LEFT JOIN " +
                "(SELECT film_id, COUNT(user_id) as cf FROM film_like GROUP BY film_id ORDER BY COUNT(user_id)) " +
                "AS fl ON f.film_id = fl.film_id " +
                "ORDER BY fl.cf DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(mostLikeFilmQuery, this::makeFilm, limit);

        return connectingAllFilmGenre(films);
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

    private List<Film> connectingAllFilmGenre(List<Film> films) {
        List<HashMap<Integer, Genre>> genre = jdbcTemplate.query(
                "SELECT fg.*, g.name FROM film_genres as fg JOIN genre as g ON fg.genre_id = g.genre_id",
                this::getAllGenres);

        for (Film film : films) {
            Set<Genre> genres = new HashSet<>();
            for (HashMap<Integer, Genre> hm : genre) {
                if (hm.containsKey(film.getId())) {
                    genres.add(hm.get(film.getId()));
                }
            }
            film.setGenres(genres);
        }

        return films;
    }

    private HashMap<Integer, Genre> getAllGenres(ResultSet rs, int rn) throws SQLException {
        HashMap<Integer, Genre> genres = new HashMap<>();
        genres.put(rs.getInt("film_id"),
                new Genre(rs.getInt("genre_id"), rs.getString("name")));

        return genres;
    }

    private Set<Genre> getSpecificGenres(int filmId) {
        RowMapper<Genre> rowMapper = (rs, rn) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name"));

        return new HashSet<>(jdbcTemplate.query(
                "SELECT fg.*, g.name " +
                        "FROM film_genres as fg " +
                        "JOIN genre as g ON fg.genre_id = g.genre_id WHERE film_id = ?",
                rowMapper, filmId));
    }

    private Film makeFilm(ResultSet rs, int rn) throws SQLException {
        int filmId = rs.getInt("film_id");
        Mpa mpa = new Mpa(rs.getInt("rating"), rs.getString("name"));

        return new Film(
                filmId,
                rs.getString("film_name"),
                rs.getString("film_description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration"),
                mpa,
                null
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

        return film.getGenres();
    }
}

