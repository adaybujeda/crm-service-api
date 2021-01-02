package com.agilemonkeys.crm.storage;

import com.agilemonkeys.crm.CrmServiceApiApplication;
import com.agilemonkeys.crm.CrmServiceApiConfiguration;
import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.domain.UserRole;
import io.dropwizard.testing.ConfigOverride;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UsersDaoTest {

    @ClassRule
    public static final DropwizardAppRule<CrmServiceApiConfiguration> RULE =
            new DropwizardAppRule<>(CrmServiceApiApplication.class, "/config.yml",
                    ConfigOverride.config("server.applicationConnectors[0].port", "0"),
                    ConfigOverride.config("server.adminConnectors[0].port", "0"));

    private static UsersDao underTest;

    @BeforeClass
    public static void beforeTest() throws Exception {
        RULE.getApplication().run("db", "migrate", "/config.yml");
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
        User updatedUser = new User(oldUser.getUserId(), "newName", "newUsername", "newPassword", UserRole.ADMIN, 10, oldUser.getCreatedDate(), LocalDateTime.now());
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
        User updatedUser = new User(oldUser.getUserId(), "newName", "newUsername", "newPassword", UserRole.ADMIN, 10, oldUser.getCreatedDate(), LocalDateTime.now());
        int updatedRows = underTest.updateUser(updatedUser, 1);
        MatcherAssert.assertThat(updatedRows, Matchers.is(0));
        MatcherAssert.assertThat(oldUser.getUserId(), Matchers.is(updatedUser.getUserId()));
    }

    private User createUser(UUID userId, String username, Integer version) {
        String name = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        User newUser = new UserBuilder().withUserId(userId).withName(name).withUsername(username).withPassword(password)
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