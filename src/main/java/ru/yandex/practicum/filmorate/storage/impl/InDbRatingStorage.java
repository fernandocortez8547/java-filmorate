package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UnknownIdExeption;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class InDbRatingStorage {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Collection<Rating> getAllRatings() {
        final String sqlQuery = "SELECT * FROM rating";

        return jdbcTemplate.query(sqlQuery, this::makeMpa);
    }

    public Rating getRatingById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from rating where rating_id = ?", id);

        if(userRows.next()) {
            return new Rating(
                    userRows.getInt("rating_id"),
                    userRows.getString("name")
            );
        } else {
            throw new UnknownIdExeption("Storage don't have rating with id " + id);
        }
    }

    private Rating makeMpa(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("rating_id");
        String nameMpa = resultSet.getString("name");

        return new Rating(id, nameMpa);
    }
}
