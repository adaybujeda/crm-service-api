package com.agilemonkeys.crm.services.auth;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.services.GetUsersService;
import com.agilemonkeys.crm.services.UpdateUserService;
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
        Mockito.when(getUsersService.getUserById(USER_ID)).thenReturn(USER);
        Mockito.when(updateUserService.updateUser(USER.getVersion(), USER)).thenThrow(new RuntimeException("anything"));

        underTest.resetPassword(USER_ID, "new-password");
    }

    @Test
    public void should_update_password() {
        String newPassword = "new-password";
        Mockito.when(getUsersService.getUserById(USER_ID)).thenReturn(USER);
        Mockito.when(passwordHashService.hashPassword(newPassword)).thenReturn("PASSWORD_HASH");

        User updatedUser = UserBuilder.builder().withUserId(USER_ID).build();
        Mockito.when(updateUserService.updateUser(Mockito.eq(USER.getVersion()), Mockito.any(User.class))).thenReturn(updatedUser);

        User result = underTest.resetPassword(USER_ID, newPassword);

        MatcherAssert.assertThat(result, Matchers.is(updatedUser));
        Mockito.verify(getUsersService).getUserById(USER_ID);
        Mockito.verify(passwordHashService).hashPassword(newPassword);
        ArgumentCaptor<User> userWithNewPassword = ArgumentCaptor.forClass(User.class);
        Mockito.verify(updateUserService).updateUser(Mockito.eq(USER.getVersion()), userWithNewPassword.capture());

        MatcherAssert.assertThat(userWithNewPassword.getValue().getPasswordHash(), Matchers.is("PASSWORD_HASH"));
        MatcherAssert.assertThat(userWithNewPassword.getValue().getVersion(), Matchers.is(USER.getVersion() + 1));
        MatcherAssert.assertThat(userWithNewPassword.getValue().getUpdatedDate(), Matchers.greaterThan(USER.getUpdatedDate()));
    }
}