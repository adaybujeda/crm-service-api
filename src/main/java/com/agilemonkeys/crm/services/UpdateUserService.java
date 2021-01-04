package com.agilemonkeys.crm.services;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiStaleStateException;
import com.agilemonkeys.crm.resources.UpdateUserRequest;
import com.agilemonkeys.crm.storage.UsersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class UpdateUserService {

    private static final Logger log = LoggerFactory.getLogger(UpdateUserService.class);
    private static final String UPDATE_USER = "updateUser";
    private static final String UPDATE_ROLE = "updateRole";

    private final UsersDao usersDao;
    private final DuplicatedUserService duplicatedUserService;

    public UpdateUserService(UsersDao usersDao, DuplicatedUserService duplicatedUserService) {
        this.usersDao = usersDao;
        this.duplicatedUserService = duplicatedUserService;
    }

    public User updateUser(UUID userId, Integer requestVersion, UpdateUserRequest updateRequest) {
        User oldUser = checkExistingUserState(UPDATE_USER, userId, requestVersion);
        duplicatedUserService.checkUsername(updateRequest.getUsername(), Optional.of(userId));

        User newUser = UserBuilder.fromRequest(updateRequest)
                .withUserId(userId)
                .withPasswordHash(oldUser.getPasswordHash())
                .withVersion(oldUser.getVersion() + 1)
                .withCreatedDate(oldUser.getCreatedDate())
                .build();

        return updateUser(requestVersion, newUser);
    }

    public User updateUser(Integer currentVersion, User newUser) {
        duplicatedUserService.checkUsername(newUser.getUsername(), Optional.of(newUser.getUserId()));
        return updateStorage(UPDATE_USER, newUser, currentVersion);
    }

    public User updateRole(UUID userId, Integer requestVersion, UserRole newRole) {
        User oldUser = checkExistingUserState(UPDATE_ROLE, userId, requestVersion);
        User newUser = UserBuilder.fromUser(oldUser)
                .withRole(newRole)
                .withVersion(oldUser.getVersion() + 1)
                .withUpdatedDate(LocalDateTime.now())
                .build();

        return updateStorage(UPDATE_ROLE, newUser, requestVersion);
    }

    private User checkExistingUserState(String action, UUID userId, Integer requestVersion) {
        Optional<User> oldUser = usersDao.getUserById(userId);
        if (!oldUser.isPresent()) {
            throw new CrmServiceApiNotFoundException(String.format("UserId: %s not found", userId));
        }

        if (!oldUser.get().getVersion().equals(requestVersion)) {
            log.warn("action={} step=checkExistingUserState result=staleState userId={} requestVersion={} dbUser={}", action, userId, requestVersion, oldUser.get());
            throw new CrmServiceApiStaleStateException(String.format("Invalid state for userId: %s request version: %s found: %s", userId, requestVersion, oldUser.get().getVersion()));
        }

        return oldUser.get();
    }

    private User updateStorage(String action, User newUser, Integer fromVersion) {
        int updated = usersDao.updateUser(newUser, fromVersion);
        if (updated != 1) {
            throw new CrmServiceApiStaleStateException(String.format("Concurrent modification. UserId: %s modified since request was made. Try again", newUser.getUserId()));
        }
        log.info("action={} step=updateStorage result=success userId={} newUser={}", action, newUser.getUserId(), newUser);
        return newUser;
    }
}
