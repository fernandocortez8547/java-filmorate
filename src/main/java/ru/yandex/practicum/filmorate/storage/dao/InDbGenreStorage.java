package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UnknownIdExeption;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InDbGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Genre> getGenresList() {
        final String sqlQuery = "SELECT * FROM genre";

        return jdbcTemplate.query(sqlQuery, this::makeGenre);
    }

    public Genre getGenreById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from genre where genre_id = ?", id);

        if(userRows.next()) {
            return new Genre(
                    userRows.getInt("genre_id"),
                    userRows.getString("name")
            );
        } else {
            throw new UnknownIdExeption("Storage don't have genre with id " + id);
        }
    }

    private Genre makeGenre(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("genre_id");
        String name = resultSet.getString("name");

        return new Genre(id, name);
    }
}