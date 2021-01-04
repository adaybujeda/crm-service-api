package com.agilemonkeys.crm.services.auth;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.services.GetUsersService;
import com.agilemonkeys.crm.services.UpdateUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.UUID;

public class ResetPasswordService {

    private static final Logger log = LoggerFactory.getLogger(ResetPasswordService.class);

    private final GetUsersService getUsersService;
    private final UpdateUserService updateUserService;
    private final CrmPasswordHashService passwordHashService;

    public ResetPasswordService(GetUsersService getUsersService, UpdateUserService updateUserService, CrmPasswordHashService passwordHashService) {
        this.getUsersService = getUsersService;
        this.updateUserService = updateUserService;
        this.passwordHashService = passwordHashService;
    }

    public User resetPassword(UUID userId, String newPassword) {
        User oldUser = getUsersService.getUserById(userId);
        String passwordHash = passwordHashService.hashPassword(newPassword);

        User userWithNewPassword = UserBuilder.fromUser(oldUser)
                .withPasswordHash(passwordHash)
                .withVersion(oldUser.getVersion() + 1)
                .withUpdatedDate(LocalDateTime.now())
                .build();
        User updatedUser = updateUserService.updateUser(oldUser.getVersion(), userWithNewPassword);
        log.info("action=resetPassword result=success userId={} updatedUser={}", updatedUser.getUserId(), updatedUser);
        return updatedUser;
    }
}
