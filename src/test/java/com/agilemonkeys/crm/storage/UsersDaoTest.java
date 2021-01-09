package com.agilemonkeys.crm.storage;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UsersDaoTest extends RunningServiceBaseTest {

    private static UsersDao underTest;

    @BeforeClass
    public static void beforeTest() throws Exception {
        Jdbi jdbi = Jdbi.create("jdbc:h2:mem:test", "sa", "sa");
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.registerRowMapper(ConstructorMapper.factory(User.class));

        underTest = jdbi.onDemand(UsersDao.class);
    }


    @Test
    public void should_store_user_in_database() {
        String username = UUID.randomUUID().toString();
        insertUser(UUID.randomUUID(), username);
    }

    @Test
    public void should_find_user_by_userId() {
        String username = UUID.randomUUID().toString();
        User newUser = insertUser(UUID.randomUUID(), username);
        Optional<User> userById = underTest.getUserById(newUser.getUserId());
        MatcherAssert.assertThat(userById.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(userById.get(), Matchers.is(newUser));
    }

    @Test
    public void should_find_user_by_username() {
        String username = UUID.randomUUID().toString();
        User newUser = insertUser(UUID.randomUUID(), username);
        Optional<User> userByUsername= underTest.getUserByUsername(username);
        MatcherAssert.assertThat(userByUsername.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(userByUsername.get(), Matchers.is(newUser));
    }

    @Test
    public void should_get_all_users_in_database() {
        String username = UUID.randomUUID().toString();
        User newUser = insertUser(UUID.randomUUID(), username);
        List<User> users = underTest.getUsers();
        MatcherAssert.assertThat(users.isEmpty(), Matchers.is(false));
        MatcherAssert.assertThat(users.size(), Matchers.greaterThan(0));
    }

    @Test
    public void should_update_user_when_old_version_match() {
        String username = UUID.randomUUID().toString();
        User oldUser = insertUser(UUID.randomUUID(), username, 1);
        User updatedUser =UserBuilder.fromUser(oldUser)
                .withName("newName").withNormalizedUsername("newUsername").withPasswordHash("mnewPassword")
                .withUpdatedDate().build();
        int updatedRows = underTest.updateUser(updatedUser, oldUser.getVersion());
        MatcherAssert.assertThat(updatedRows, Matchers.is(1));

        Optional<User> userById = underTest.getUserById(oldUser.getUserId());
        MatcherAssert.assertThat(userById.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(userById.get(), Matchers.is(updatedUser));
    }

    @Test
    public void should_not_update_user_when_old_version_does_not_match() {
        String username = UUID.randomUUID().toString();
        User oldUser = insertUser(UUID.randomUUID(), username, 2);
        User updatedUser =UserBuilder.fromUser(oldUser)
                .withName("newName").withNormalizedUsername("newUsername").withPasswordHash("mnewPassword")
                .withUpdatedDate().build();
        int updatedRows = underTest.updateUser(updatedUser, 1);
        MatcherAssert.assertThat(updatedRows, Matchers.is(0));
        MatcherAssert.assertThat(oldUser.getUserId(), Matchers.is(updatedUser.getUserId()));
    }

    @Test
    public void should_delete_user() {
        String username = UUID.randomUUID().toString();
        User newUser = insertUser(UUID.randomUUID(), username);
        Optional<User> userById = underTest.getUserById(newUser.getUserId());
        MatcherAssert.assertThat(userById.isPresent(), Matchers.is(true));

        int deletedUsers = underTest.deleteUser(newUser.getUserId());
        MatcherAssert.assertThat(deletedUsers, Matchers.is(1));

        userById = underTest.getUserById(newUser.getUserId());
        MatcherAssert.assertThat(userById.isPresent(), Matchers.is(true));
        MatcherAssert.assertThat(userById.get().getDeletedDate(), Matchers.notNullValue());
    }

    private User createUser(UUID userId, String username, Integer version) {
        String name = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        User newUser = UserBuilder.builder().withUserId(userId).withName(name).withNormalizedUsername(username).withPasswordHash(password)
                .withVersion(version).build();
        return newUser;
    }

    private User insertUser(UUID userId, String username, Integer version) {
        User newUser = createUser(userId, username, version);
        int rowsInserted = underTest.insertUser(newUser);
        MatcherAssert.assertThat(rowsInserted, CoreMatchers.is(1));
        return newUser;
    }

    private User insertUser(UUID userId, String username) {
        return insertUser(userId, username, 1);
    }

}