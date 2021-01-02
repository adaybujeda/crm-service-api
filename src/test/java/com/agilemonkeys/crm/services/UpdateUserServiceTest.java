package com.agilemonkeys.crm.services;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiStaleStateException;
import com.agilemonkeys.crm.resources.CreateUpdateUserRequest;
import com.agilemonkeys.crm.storage.UsersDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

public class UpdateUserServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();

    private UsersDao usersDao = Mockito.mock(UsersDao.class);
    private UpdateUserService underTest = new UpdateUserService(usersDao);

    @Test(expected = CrmServiceApiNotFoundException.class)
    public void should_throw_not_found_exception_when_userId_not_in_database() {
        underTest.updateUser(USER_ID, 10, createRequest());
    }

    @Test(expected = CrmServiceApiStaleStateException.class)
    public void should_throw_stale_state_exception_when_request_version_is_different_from_database() {
        Integer staleVersion = 10;
        User newVersionUser = new UserBuilder().withVersion(11).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(newVersionUser));

        CreateUpdateUserRequest request = createRequest();
        underTest.updateUser(USER_ID, staleVersion, request);
    }

    @Test(expected = CrmServiceApiStaleStateException.class)
    public void should_throw_stale_state_exception_when_database_update_returns_0() {
        Integer version = 10;
        User dbUser = new UserBuilder().withUserId(USER_ID).withVersion(version).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(dbUser));
        Mockito.when(usersDao.updateUser(Mockito.any(User.class), Mockito.eq(version))).thenReturn(0);

        CreateUpdateUserRequest request = createRequest();
        underTest.updateUser(USER_ID, version, request);
    }

    @Test
    public void should_update_user_when_version_matches() {
        Integer version = 10;
        User dbUser = new UserBuilder().withUserId(USER_ID).withVersion(version).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(dbUser));
        Mockito.when(usersDao.updateUser(Mockito.any(User.class), Mockito.eq(version))).thenReturn(1);

        CreateUpdateUserRequest request = createRequest();
        User result = underTest.updateUser(USER_ID, version, request);

        ArgumentCaptor<User> resultInDatabase = ArgumentCaptor.forClass(User.class);
        Mockito.verify(usersDao).updateUser(resultInDatabase.capture(), Mockito.eq(version));

        assertUserFields(result, dbUser, request);
        assertUserFields(resultInDatabase.getValue(), dbUser, request);
    }

    private void assertUserFields(User updatedUser, User previousVersionInDB, CreateUpdateUserRequest request) {
        MatcherAssert.assertThat(updatedUser.getUserId(), Matchers.is(previousVersionInDB.getUserId()));
        MatcherAssert.assertThat(updatedUser.getCreatedDate(), Matchers.is(previousVersionInDB.getCreatedDate()));
        MatcherAssert.assertThat(updatedUser.getUpdatedDate(), Matchers.notNullValue());
        MatcherAssert.assertThat(updatedUser.getUpdatedDate(), Matchers.not(Matchers.is(previousVersionInDB.getUpdatedDate())));
        MatcherAssert.assertThat(updatedUser.getVersion(), Matchers.is(previousVersionInDB.getVersion() + 1));

        MatcherAssert.assertThat(updatedUser.getName(), Matchers.is(request.getName()));
        MatcherAssert.assertThat(updatedUser.getUsername(), Matchers.is(request.getUsername()));
        MatcherAssert.assertThat(updatedUser.getPassword(), Matchers.is(request.getPassword()));
        MatcherAssert.assertThat(updatedUser.getRole(), Matchers.is(request.getRole()));
    }

    private CreateUpdateUserRequest createRequest() {
        return new CreateUpdateUserRequest("name", "username", "password", UserRole.ADMIN);
    }

}