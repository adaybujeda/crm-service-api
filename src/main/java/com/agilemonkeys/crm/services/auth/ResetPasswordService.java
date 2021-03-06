package com.agilemonkeys.crm.services.auth;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.exceptions.CrmServiceApiAuthException;
import com.agilemonkeys.crm.services.user.GetUsersService;
import com.agilemonkeys.crm.services.user.UpdateUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        if (oldUser.isDeleted()) {
            log.warn("action=resetPassword result=deleted-user userId={}", userId);
            throw new CrmServiceApiAuthException("Invalid credentials");
        }

        String passwordHash = passwordHashService.hashPassword(newPassword);

        User updatedUser = updateUserService.resetPassword(userId, oldUser.getVersion(), passwordHash);
        log.info("action=resetPassword result=success userId={} updatedUser={}", updatedUser.getUserId(), updatedUser);
        return updatedUser;
    }
}
