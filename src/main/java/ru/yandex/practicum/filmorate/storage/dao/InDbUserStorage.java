package ru.yandex.practicum.filmorate.storage.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UnknownIdExeption;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class InDbUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        user.setName(nameValidate(user));

        final String userAddQuery = "INSERT INTO \"user\" (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(userAddQuery, new String[]{"user_id"});
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getLogin());
            statement.setString(3, user.getName());
            statement.setDate(4, Date.valueOf(user.getBirthday()));

            return statement;
        }, keyHolder);

        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return user;
    }

    @Override
    public User updateUser(User user) {
        user.setName(nameValidate(user));

        final String findUserQuery = "SELECT * FROM \"user\" WHERE user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(findUserQuery, user.getId());

        if (!rowSet.next()) {
            throw new UnknownIdExeption("Storage don't have user with id " + user.getId());
        }

        final String updateUserQuery = "UPDATE \"user\" SET email = ?, login = ?," +
                " name = ?, birthday = ? WHERE user_id = ?";

        jdbcTemplate.update(updateUserQuery, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getId());

        return user;
    }

    @Override
    public List<User> getUsersList() {
        return jdbcTemplate.query("SELECT * FROM \"user\"", this::makeUser);
    }

    @Override
    public User getUser(int id) {
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet("SELECT * FROM \"user\" WHERE user_id = ?", id);

        if (!rowSet.next()) {
            throw new UnknownIdExeption("Storage don't have user with id " + id);
        }

        return jdbcTemplate.queryForObject("SELECT * FROM \"user\" WHERE user_id = ?", this::makeUser, id);
    }

    @Override
    public void removeUser(int id) {
        jdbcTemplate.update("DELETE FROM friends_request WHERE user_id = ?", id);
        jdbcTemplate.update("DELETE FROM friends_request WHERE friend_id = ?", id);
        jdbcTemplate.update("DELETE FROM \"user\" WHERE user_id = ?", id);
    }

    @Override
    public List<Integer> addFriendship(int userId, int friendId) {
        validate(userId, friendId);

        final String firstUserQuery;
        final String secondUserQuery;

        if (findFriendship(userId, friendId)) {
            firstUserQuery = "UPDATE friends_request SET status = ? WHERE user_id = ? AND friend_id = ?";
            jdbcTemplate.update(firstUserQuery, true, friendId, userId);
        } else {
            firstUserQuery = "INSERT INTO friends_request (user_id, friend_id, status) " + "VALUES (?, ?, ?)";
            secondUserQuery = "INSERT INTO friends_request (user_id, friend_id, status)" + "VALUES (?, ?, ?)";
            jdbcTemplate.update(firstUserQuery, userId, friendId, false);
            jdbcTemplate.update(secondUserQuery, friendId, userId, true);
        }

        return List.of(userId, friendId);
    }

    @Override
    public List<User> getUserFriends(int id) {
        final String getFriendsQuery = "SELECT * FROM \"user\" AS u " +
                "LEFT JOIN friends_request AS fr ON u.user_id = fr.friend_id " +
                "WHERE fr.status = false AND fr.user_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(getFriendsQuery, id);

        if (rowSet.next())
            return jdbcTemplate.query(getFriendsQuery, this::makeUser, id);
        else
            return Collections.emptyList();
    }

    @Override
    public void removeFriendship(int userId, int friendId) {
        final String deleteUserIdQuery = "DELETE FROM friends_request WHERE user_id = ? AND friend_id = ?";
        final String deleteFriendIdQuery = "DELETE FROM friends_request WHERE friend_id = ? AND user_id = ?";

        jdbcTemplate.update(deleteFriendIdQuery, userId, friendId);
        jdbcTemplate.update(deleteUserIdQuery, userId, friendId);
    }

    @Override
    public List<User> getCommonFriend(int userId, int friendId) {
        final String commonFriendQuery = "SELECT * FROM \"user\" AS u " +
                "LEFT JOIN " +
                "(SELECT friend_id FROM friends_request as fr1 WHERE fr1.user_id = ? AND fr1.status = false)" +
                " AS fr2 ON u.user_id = fr2.friend_id " +
                "WHERE fr2.friend_id IN " +
                "(SELECT friend_id FROM friends_request AS fr3 WHERE fr3.user_id = ? AND fr3.status = false)";

        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(commonFriendQuery, userId, friendId);

        if (rowSet.next()) {
            return jdbcTemplate.query(commonFriendQuery, this::makeUser, userId, friendId);
        } else
            return Collections.emptyList();
    }

    private boolean findFriendship(int userId, int friendId) {
        final String friendshipQuery = "SELECT * FROM friends_request WHERE user_id = ? AND friend_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(friendshipQuery, userId, friendId);
        return rowSet.next();
    }

    private void validate(int userId, int friendId) {
        final String userValidateQuery = "SELECT * FROM \"user\" WHERE user_id = ?";

        SqlRowSet userRowSet = jdbcTemplate.queryForRowSet(userValidateQuery, userId);
        SqlRowSet friendRowSet = jdbcTemplate.queryForRowSet(userValidateQuery, friendId);

        if (!userRowSet.next() || !friendRowSet.next()) {
            throw new UnknownIdExeption("Storage don't have user with id's.");
        }
    }

    private User makeUser(ResultSet rs, int rn) throws SQLException {
        int userId = rs.getInt("user_id");

        return new User(
                userId,
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
    }

    private String nameValidate(User user) {
        String name = user.getName();

        if (name == null || name.length() == 0) {
            return user.getLogin();
        }

        return user.getName();
    }
}
