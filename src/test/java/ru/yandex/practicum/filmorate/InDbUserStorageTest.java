package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.dao.InDbUserStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
public class InDbUserStorageTest {

    private final JdbcTemplate jdbcTemplate = new JdbcTemplate(CreateConfiguredDb.createEmbeddedDatabase());
    private final UserStorage inDbUserStorage = new InDbUserStorage(jdbcTemplate);

    private final User user1 = User.builder()
            .email("user1@yandex.ru")
            .login("user1")
            .name("user1")
            .birthday(LocalDate.of(2005, 12, 16))
            .build();

    private final User user2 = User.builder()
            .email("user2@yandex.ru")
            .login("user2")
            .name("user2")
            .birthday(LocalDate.of(2004, 11, 15))
            .build();

    private final User user3 = User.builder()
            .email("user3@yandex.ru")
            .login("user3")
            .name("user3")
            .birthday(LocalDate.of(2003, 11, 14))
            .build();

    @Test
    public void addUserTest() {
        User user = inDbUserStorage.addUser(user1);

        assertThat(user).extracting("id").isNotNull();
        assertThat(user).hasFieldOrPropertyWithValue("email", "user1@yandex.ru");
        assertThat(user).hasFieldOrPropertyWithValue
                ("login", "user1");
        assertThat(user).hasFieldOrPropertyWithValue("birthday",
                LocalDate.of(2005, 12, 16));
        assertThat(user).hasFieldOrPropertyWithValue
                ("name", "user1");
    }

    @Test
    public void updateUserTest() {
        int userId = inDbUserStorage.addUser(user1).getId();

        user2.setId(userId);

        inDbUserStorage.updateUser(user2);

        assertThat(user2).extracting("id").isNotNull();
        assertThat(user2).hasFieldOrPropertyWithValue("email", "user2@yandex.ru");
        assertThat(user2).hasFieldOrPropertyWithValue
                ("login", "user2");
        assertThat(user2).hasFieldOrPropertyWithValue("birthday",
                LocalDate.of(2004, 11, 15));
        assertThat(user2).hasFieldOrPropertyWithValue
                ("name", "user2");
    }

    @Test
    public void getUsersTest() {
        inDbUserStorage.addUser(user1);

        assertThat(inDbUserStorage.getUsersList()).hasSize(1);
    }

    @Test
    public void getFilmTest() {
        int userId = inDbUserStorage.addUser(user1).getId();

        assertThat(inDbUserStorage.getUser(userId)).hasFieldOrPropertyWithValue("id", userId);
    }

    @Test
    public void removeFilmTest() {
        int userId = inDbUserStorage.addUser(user1).getId();
        inDbUserStorage.removeUser(userId);

        assertThat(inDbUserStorage.getUsersList()).hasSize(0);
    }

    @Test
    public void addFriendShipTest() {
        int user1Id = inDbUserStorage.addUser(user1).getId();
        int user2Id = inDbUserStorage.addUser(user2).getId();

        inDbUserStorage.addFriendship(user1Id, user2Id);

        Assertions.assertNull(inDbUserStorage.getUserFriends(user1Id));
        assertThat(inDbUserStorage.getUserFriends(user2Id)).hasSize(1);

        inDbUserStorage.addFriendship(user2Id, user1Id);

        assertThat(inDbUserStorage.getUserFriends(user1Id)).hasSize(1);
    }

    @Test
    public void removeFriendShipTest() {
        int user1Id = inDbUserStorage.addUser(user1).getId();
        int user2Id = inDbUserStorage.addUser(user2).getId();

        inDbUserStorage.addFriendship(user1Id, user2Id);
        inDbUserStorage.addFriendship(user2Id, user1Id);

        inDbUserStorage.removeFriendship(user1Id, user2Id);

        Assertions.assertNull(inDbUserStorage.getUserFriends(user1Id));
        Assertions.assertNull(inDbUserStorage.getUserFriends(user2Id));
    }

    @Test
    public void getFriendsTest() {
        int user1Id = inDbUserStorage.addUser(user1).getId();
        int user2Id = inDbUserStorage.addUser(user2).getId();

        inDbUserStorage.addFriendship(user1Id, user2Id);
        inDbUserStorage.addFriendship(user2Id, user1Id);

        assertThat(inDbUserStorage.getUserFriends(user1Id)).hasSize(1);
        assertThat(inDbUserStorage.getUserFriends(user2Id)).hasSize(1);
    }

    @Test
    public void getCommonFriends() {
        int user1Id = inDbUserStorage.addUser(user1).getId();
        int user2Id = inDbUserStorage.addUser(user2).getId();
        int user3Id = inDbUserStorage.addUser(user3).getId();

        inDbUserStorage.addFriendship(user1Id, user3Id);
        inDbUserStorage.addFriendship(user3Id, user1Id);
        inDbUserStorage.addFriendship(user1Id, user2Id);
        inDbUserStorage.addFriendship(user2Id, user1Id);

        List<User> commonFriends = inDbUserStorage.getCommonFriend(user2Id, user3Id);

        assertThat(commonFriends.get(0)).hasFieldOrPropertyWithValue("id", user1Id);
    }
}
