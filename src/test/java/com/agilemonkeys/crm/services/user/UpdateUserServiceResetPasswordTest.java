package com.agilemonkeys.crm.services.user;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.exceptions.CrmServiceApiDeletedException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiStaleStateException;
import com.agilemonkeys.crm.storage.UsersDao;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

public class UpdateUserServiceResetPasswordTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final String NEW_PASSWORD = UUID.randomUUID().toString();

    private UsersDao usersDao = Mockito.mock(UsersDao.class);
    private DuplicatedUserService duplicatedUserService = Mockito.mock(DuplicatedUserService.class);

    private UpdateUserService underTest = new UpdateUserService(usersDao, duplicatedUserService);

    @Test(expected = CrmServiceApiNotFoundException.class)
    public void resetPassword_should_throw_not_found_exception_when_userId_not_in_database() {
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.empty());
        underTest.resetPassword(USER_ID, 10, NEW_PASSWORD);
    }

    @Test(expected = CrmServiceApiStaleStateException.class)
    public void resetPassword_should_throw_stale_state_exception_when_request_version_is_different_from_database() {
        Integer staleVersion = 10;
        User newVersionUser = UserBuilder.builder().withVersion(11).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(newVersionUser));

        underTest.resetPassword(USER_ID, staleVersion, NEW_PASSWORD);
    }

    @Test(expected = CrmServiceApiStaleStateException.class)
    public void resetPassword_should_throw_stale_state_exception_when_database_update_returns_0() {
        Integer version = 10;
        User dbUser = UserBuilder.builder().withUserId(USER_ID).withVersion(version).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(dbUser));
        Mockito.when(usersDao.updateUser(Mockito.any(User.class), Mockito.eq(version))).thenReturn(0);

        underTest.resetPassword(USER_ID, version, NEW_PASSWORD);
    }

    @Test(expected = CrmServiceApiDeletedException.class)
    public void resetPassword_should_throw_deleted_exception_when_when_user_is_deleted() {
        Integer version = 10;
        User dbUser = UserBuilder.builder().withVersion(version).withDeletedDate().build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(dbUser));
        Mockito.when(usersDao.updateUser(Mockito.any(User.class), Mockito.eq(version))).thenReturn(1);

        underTest.resetPassword(USER_ID, version, NEW_PASSWORD);
    }

    @Test
    public void resetPassword_should_update_password_when_version_matches() {
        Integer version = 10;
        User dbUser = UserBuilder.builder().withUserId(USER_ID).withVersion(version).withPasswordHash(NEW_PASSWORD).build();
        Mockito.when(usersDao.getUserById(USER_ID)).thenReturn(Optional.of(dbUser));
        Mockito.when(usersDao.updateUser(Mockito.any(User.class), Mockito.eq(version))).thenReturn(1);

        String newPassword = UUID.randomUUID().toString();
        User result = underTest.resetPassword(USER_ID, version, newPassword);

        ArgumentCaptor<User> resultInDatabase = ArgumentCaptor.forClass(User.class);
        Mockito.verify(usersDao).updateUser(resultInDatabase.capture(), Mockito.eq(version));

        assertUpdateRoleUserFields(result, newPassword, dbUser);
        assertUpdateRoleUserFields(resultInDatabase.getValue(), newPassword, dbUser);
    }

    private void assertUpdateRoleUserFields(User updatedUser, String newPassword, User previousVersionInDB) {
        MatcherAssert.assertThat(updatedUser.getUserId(), Matchers.is(previousVersionInDB.getUserId()));
        MatcherAssert.assertThat(updatedUser.getCreatedDate(), Matchers.is(previousVersionInDB.getCreatedDate()));
        MatcherAssert.assertThat(updatedUser.getUpdatedDate(), Matchers.notNullValue());
        MatcherAssert.assertThat(updatedUser.getUpdatedDate(), Matchers.not(Matchers.is(previousVersionInDB.getUpdatedDate())));
        MatcherAssert.assertThat(updatedUser.getVersion(), Matchers.is(previousVersionInDB.getVersion() + 1));

        MatcherAssert.assertThat(updatedUser.getName(), Matchers.is(previousVersionInDB.getName()));
        MatcherAssert.assertThat(updatedUser.getUsername(), Matchers.is(previousVersionInDB.getUsername()));
        MatcherAssert.assertThat(updatedUser.getRole(), Matchers.is(previousVersionInDB.getRole()));

        MatcherAssert.assertThat(updatedUser.getPasswordHash(), Matchers.is(newPassword));
    }
}