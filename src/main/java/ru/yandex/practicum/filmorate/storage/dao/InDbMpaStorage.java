package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UnknownIdExeption;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class InDbMpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> getMpaList() {
        final String sqlQuery = "SELECT * FROM rating";

        return jdbcTemplate.query(sqlQuery, this::makeMpa);
    }

    public Mpa getMpaById(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from rating where rating_id = ?", id);

        if(userRows.next()) {
            return new Mpa(
                    userRows.getInt("rating_id"),
                    userRows.getString("name")
            );
        } else {
            throw new UnknownIdExeption("Storage don't have rating with id " + id);
        }
    }

    private Mpa makeMpa(ResultSet resultSet, int rowNum) throws SQLException {
        int id = resultSet.getInt("rating_id");
        String nameMpa = resultSet.getString("name");

        return new Mpa(id, nameMpa);
    }
}
