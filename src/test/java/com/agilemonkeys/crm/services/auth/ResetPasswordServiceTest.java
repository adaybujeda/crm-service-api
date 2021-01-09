package com.agilemonkeys.crm.services.auth;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.exceptions.CrmServiceApiAuthException;
import com.agilemonkeys.crm.services.user.GetUsersService;
import com.agilemonkeys.crm.services.user.UpdateUserService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.UUID;

public class ResetPasswordServiceTest {

    private static final UUID USER_ID = UUID.randomUUID();
    private static final User USER = UserBuilder.builder().withUserId(USER_ID).build();

    private GetUsersService getUsersService = Mockito.mock(GetUsersService.class);
    private UpdateUserService updateUserService = Mockito.mock(UpdateUserService.class);
    private CrmPasswordHashService passwordHashService = Mockito.mock(CrmPasswordHashService.class);

    private ResetPasswordService underTest = new ResetPasswordService(getUsersService, updateUserService, passwordHashService);

    @Test(expected = RuntimeException.class)
    public void should_propagate_exceptions_from_GetUsersService() {
        Mockito.when(getUsersService.getUserById(USER_ID)).thenThrow(new RuntimeException("anything"));

        underTest.resetPassword(USER_ID, "new-password");
    }

    @Test(expected = RuntimeException.class)
    public void should_propagate_exceptions_from_UpdateUserService() {
        String newPassword = "new-password";
        Mockito.when(getUsersService.getUserById(USER_ID)).thenReturn(USER);
        Mockito.when(passwordHashService.hashPassword(newPassword)).thenReturn("PASSWORD_HASH");
        Mockito.when(updateUserService.resetPassword(USER_ID, USER.getVersion(), "PASSWORD_HASH")).thenThrow(new RuntimeException("anything"));

        underTest.resetPassword(USER_ID, newPassword);
    }

    @Test(expected = CrmServiceApiAuthException.class)
    public void should_throw_auth_exception_when_user_is_deleted() {
        String newPassword = "new-password";
        User deletedUser = UserBuilder.builder().withDeletedDate().build();
        Mockito.when(getUsersService.getUserById(USER_ID)).thenReturn(deletedUser);

        underTest.resetPassword(USER_ID, newPassword);
    }

    @Test
    public void should_update_password() {
        String newPassword = "new-password";
        Mockito.when(getUsersService.getUserById(USER_ID)).thenReturn(USER);
        Mockito.when(passwordHashService.hashPassword(newPassword)).thenReturn("PASSWORD_HASH");

        User updatedUser = UserBuilder.builder().withUserId(USER_ID).build();
        Mockito.when(updateUserService.resetPassword(USER_ID, USER.getVersion(), "PASSWORD_HASH")).thenReturn(updatedUser);

        User result = underTest.resetPassword(USER_ID, newPassword);

        MatcherAssert.assertThat(result, Matchers.is(updatedUser));
        Mockito.verify(getUsersService).getUserById(USER_ID);
        Mockito.verify(passwordHashService).hashPassword(newPassword);
        ArgumentCaptor<User> userWithNewPassword = ArgumentCaptor.forClass(User.class);
        Mockito.verify(updateUserService).resetPassword(USER_ID, USER.getVersion(), "PASSWORD_HASH");
    }
}