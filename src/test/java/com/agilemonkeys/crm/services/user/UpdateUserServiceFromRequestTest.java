package com.agilemonkeys.crm.services.user;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.exceptions.CrmServiceApiDeletedException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiDuplicatedException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiStaleStateException;
import com.agilemonkeys.crm.resources.user.UpdateUserRequest;
import com.agilemonkeys.crm.storage.UsersDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

public class UpdateUserServiceFromRequestTest {

    private static final UUID USER_ID = UUID.randomUUID();

    private UsersDao usersDao = Mockito.mock(UsersDao.class);
    private DuplicatedUserService duplicatedUserService = Mockito.mock(DuplicatedUserService.class);

    private UpdateUserService underTest = new UpdateUserService(usersDao, duplicatedUserService);

    @Test(expected = CrmServiceApiNotFoundException.class)
    public void updateUser_should_throw_not_found_exception_when_userId_not_in_database() {
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.empty());
        underTest.updateUser(USER_ID, 10, createRequest());
    }

    @Test(expected = CrmServiceApiStaleStateException.class)
    public void updateUser_should_throw_stale_state_exception_when_request_version_is_different_from_database() {
        Integer staleVersion = 10;
        User newVersionUser = UserBuilder.builder().withVersion(11).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(newVersionUser));

        UpdateUserRequest request = createRequest();
        underTest.updateUser(USER_ID, staleVersion, request);
    }

    @Test(expected = CrmServiceApiStaleStateException.class)
    public void updateUser_should_throw_stale_state_exception_when_database_update_returns_0() {
        Integer version = 10;
        User dbUser = UserBuilder.builder().withUserId(USER_ID).withVersion(version).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(dbUser));
        Mockito.when(usersDao.updateUser(Mockito.any(User.class), Mockito.eq(version))).thenReturn(0);

        UpdateUserRequest request = createRequest();
        underTest.updateUser(USER_ID, version, request);
    }

    @Test(expected = CrmServiceApiDuplicatedException.class)
    public void updateUser_should_propagate_exception_when_duplicatedUserService_throws_one() {
        Integer version = 10;
        UpdateUserRequest request = createRequest();
        User dbUser = UserBuilder.builder().withUserId(USER_ID).withVersion(version).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(dbUser));
        Mockito.doThrow(new CrmServiceApiDuplicatedException("test")).when(duplicatedUserService).checkUsername(request.getUsername(), Optional.of(USER_ID));

        underTest.updateUser(USER_ID, version, request);
    }

    @Test(expected = CrmServiceApiDeletedException.class)
    public void updateUser_should_throw_deleted_exception_when_when_user_is_deleted() {
        Integer version = 10;
        User dbUser = UserBuilder.builder().withVersion(version).withDeletedDate().build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(dbUser));
        Mockito.when(usersDao.updateUser(Mockito.any(User.class), Mockito.eq(version))).thenReturn(1);

        UpdateUserRequest request = createRequest();
        underTest.updateUser(USER_ID, version, request);
    }

    @Test
    public void updateUser_should_update_user_when_version_matches() {
        Integer version = 10;
        User dbUser = UserBuilder.builder().withUserId(USER_ID).withVersion(version).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(dbUser));
        Mockito.when(usersDao.updateUser(Mockito.any(User.class), Mockito.eq(version))).thenReturn(1);

        UpdateUserRequest request = createRequest();
        User result = underTest.updateUser(USER_ID, version, request);

        ArgumentCaptor<User> resultInDatabase = ArgumentCaptor.forClass(User.class);
        Mockito.verify(usersDao).updateUser(resultInDatabase.capture(), Mockito.eq(version));

        assertUserFields(result, dbUser, request);
        assertUserFields(resultInDatabase.getValue(), dbUser, request);
    }

    private void assertUserFields(User updatedUser, User previousVersionInDB, UpdateUserRequest request) {
        MatcherAssert.assertThat(updatedUser.getUserId(), Matchers.is(previousVersionInDB.getUserId()));
        MatcherAssert.assertThat(updatedUser.getPasswordHash(), Matchers.is(previousVersionInDB.getPasswordHash()));
        MatcherAssert.assertThat(updatedUser.getCreatedDate(), Matchers.is(previousVersionInDB.getCreatedDate()));
        MatcherAssert.assertThat(updatedUser.getUpdatedDate(), Matchers.notNullValue());
        MatcherAssert.assertThat(updatedUser.getUpdatedDate(), Matchers.not(Matchers.is(previousVersionInDB.getUpdatedDate())));
        MatcherAssert.assertThat(updatedUser.getVersion(), Matchers.is(previousVersionInDB.getVersion() + 1));

        MatcherAssert.assertThat(updatedUser.getName(), Matchers.is(request.getName()));
        MatcherAssert.assertThat(updatedUser.getUsername(), Matchers.is(request.getUsername()));
        MatcherAssert.assertThat(updatedUser.getRole(), Matchers.is(request.getRole()));
    }

    private UpdateUserRequest createRequest() {
        return new UpdateUserRequest("name", "username", UserRole.ADMIN);
    }

}