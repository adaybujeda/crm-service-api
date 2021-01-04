package com.agilemonkeys.crm.services;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.exceptions.CrmServiceApiDuplicatedException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiStaleStateException;
import com.agilemonkeys.crm.resources.UpdateUserRequest;
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
        User newVersionUser = new UserBuilder().withVersion(11).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(newVersionUser));

        UpdateUserRequest request = createRequest();
        underTest.updateUser(USER_ID, staleVersion, request);
    }

    @Test(expected = CrmServiceApiStaleStateException.class)
    public void updateUser_should_throw_stale_state_exception_when_database_update_returns_0() {
        Integer version = 10;
        User dbUser = new UserBuilder().withUserId(USER_ID).withVersion(version).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(dbUser));
        Mockito.when(usersDao.updateUser(Mockito.any(User.class), Mockito.eq(version))).thenReturn(0);

        UpdateUserRequest request = createRequest();
        underTest.updateUser(USER_ID, version, request);
    }

    @Test(expected = CrmServiceApiDuplicatedException.class)
    public void updateUser_should_propagate_exception_when_duplicatedUserService_throws_one() {
        Integer version = 10;
        UpdateUserRequest request = createRequest();
        User dbUser = new UserBuilder().withUserId(USER_ID).withVersion(version).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(dbUser));
        Mockito.doThrow(new CrmServiceApiDuplicatedException("test")).when(duplicatedUserService).checkUsername(request.getUsername(), Optional.of(USER_ID));

        underTest.updateUser(USER_ID, version, request);
    }

    @Test
    public void updateUser_should_update_user_when_version_matches() {
        Integer version = 10;
        User dbUser = new UserBuilder().withUserId(USER_ID).withVersion(version).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(dbUser));
        Mockito.when(usersDao.updateUser(Mockito.any(User.class), Mockito.eq(version))).thenReturn(1);

        UpdateUserRequest request = createRequest();
        User result = underTest.updateUser(USER_ID, version, request);

        ArgumentCaptor<User> resultInDatabase = ArgumentCaptor.forClass(User.class);
        Mockito.verify(usersDao).updateUser(resultInDatabase.capture(), Mockito.eq(version));

        assertUserFields(result, dbUser, request);
        assertUserFields(resultInDatabase.getValue(), dbUser, request);
    }

    @Test(expected = CrmServiceApiNotFoundException.class)
    public void updateRole_should_throw_not_found_exception_when_userId_not_in_database() {
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.empty());
        underTest.updateRole(USER_ID, 10, UserRole.USER);
    }

    @Test(expected = CrmServiceApiStaleStateException.class)
    public void updateRole_should_throw_stale_state_exception_when_request_version_is_different_from_database() {
        Integer staleVersion = 10;
        User newVersionUser = new UserBuilder().withVersion(11).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(newVersionUser));

        underTest.updateRole(USER_ID, staleVersion, UserRole.USER);
    }

    @Test(expected = CrmServiceApiStaleStateException.class)
    public void updateRole_should_throw_stale_state_exception_when_database_update_returns_0() {
        Integer version = 10;
        User dbUser = new UserBuilder().withUserId(USER_ID).withVersion(version).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(dbUser));
        Mockito.when(usersDao.updateUser(Mockito.any(User.class), Mockito.eq(version))).thenReturn(0);

        underTest.updateRole(USER_ID, version, UserRole.USER);
    }

    @Test
    public void updateRole_should_update_role_when_version_matches() {
        Integer version = 10;
        User dbUser = new UserBuilder().withUserId(USER_ID).withVersion(version).withRole(UserRole.USER).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(dbUser));
        Mockito.when(usersDao.updateUser(Mockito.any(User.class), Mockito.eq(version))).thenReturn(1);

        UserRole newRole = UserRole.ADMIN;
        User result = underTest.updateRole(USER_ID, version, newRole);

        ArgumentCaptor<User> resultInDatabase = ArgumentCaptor.forClass(User.class);
        Mockito.verify(usersDao).updateUser(resultInDatabase.capture(), Mockito.eq(version));

        assertUpdateRoleUserFields(result, newRole, dbUser);
        assertUpdateRoleUserFields(resultInDatabase.getValue(), newRole, dbUser);
    }

    private void assertUserFields(User updatedUser, User previousVersionInDB, UpdateUserRequest request) {
        MatcherAssert.assertThat(updatedUser.getUserId(), Matchers.is(previousVersionInDB.getUserId()));
        MatcherAssert.assertThat(updatedUser.getPassword(), Matchers.is(previousVersionInDB.getPassword()));
        MatcherAssert.assertThat(updatedUser.getCreatedDate(), Matchers.is(previousVersionInDB.getCreatedDate()));
        MatcherAssert.assertThat(updatedUser.getUpdatedDate(), Matchers.notNullValue());
        MatcherAssert.assertThat(updatedUser.getUpdatedDate(), Matchers.not(Matchers.is(previousVersionInDB.getUpdatedDate())));
        MatcherAssert.assertThat(updatedUser.getVersion(), Matchers.is(previousVersionInDB.getVersion() + 1));

        MatcherAssert.assertThat(updatedUser.getName(), Matchers.is(request.getName()));
        MatcherAssert.assertThat(updatedUser.getUsername(), Matchers.is(request.getUsername()));
        MatcherAssert.assertThat(updatedUser.getRole(), Matchers.is(request.getRole()));
    }

    private void assertUpdateRoleUserFields(User updatedUser, UserRole newRole, User previousVersionInDB) {
        MatcherAssert.assertThat(updatedUser.getUserId(), Matchers.is(previousVersionInDB.getUserId()));
        MatcherAssert.assertThat(updatedUser.getCreatedDate(), Matchers.is(previousVersionInDB.getCreatedDate()));
        MatcherAssert.assertThat(updatedUser.getUpdatedDate(), Matchers.notNullValue());
        MatcherAssert.assertThat(updatedUser.getUpdatedDate(), Matchers.not(Matchers.is(previousVersionInDB.getUpdatedDate())));
        MatcherAssert.assertThat(updatedUser.getVersion(), Matchers.is(previousVersionInDB.getVersion() + 1));

        MatcherAssert.assertThat(updatedUser.getName(), Matchers.is(previousVersionInDB.getName()));
        MatcherAssert.assertThat(updatedUser.getUsername(), Matchers.is(previousVersionInDB.getUsername()));
        MatcherAssert.assertThat(updatedUser.getPassword(), Matchers.is(previousVersionInDB.getPassword()));

        MatcherAssert.assertThat(updatedUser.getRole(), Matchers.is(newRole));
    }

    private UpdateUserRequest createRequest() {
        return new UpdateUserRequest("name", "username", UserRole.ADMIN);
    }

}