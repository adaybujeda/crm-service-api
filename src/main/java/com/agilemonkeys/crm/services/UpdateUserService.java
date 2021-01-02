package com.agilemonkeys.crm.services;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserBuilder;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.exceptions.CrmServiceApiStaleStateException;
import com.agilemonkeys.crm.resources.CreateUpdateUserRequest;
import com.agilemonkeys.crm.storage.UsersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

public class UpdateUserService {

    private static final Logger log = LoggerFactory.getLogger(UpdateUserService.class);

    private final UsersDao usersDao;

    public UpdateUserService(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    public User updateUser(UUID userId, Integer currentVersion, CreateUpdateUserRequest createRequest) {
        Optional<User> oldUser = usersDao.getUserById(userId);
        if (!oldUser.isPresent()) {
            throw new CrmServiceApiNotFoundException(String.format("UserId: %s not found", userId));
        }

        if (!oldUser.get().getVersion().equals(currentVersion)) {
            throw new CrmServiceApiStaleStateException(String.format("State state for userId: %s request version: {} found: {}", userId, currentVersion, oldUser.get().getVersion()));
        }

        User newUser = UserBuilder.fromRequest(createRequest)
                .withUserId(userId)
                .withVersion(oldUser.get().getVersion() + 1)
                .withCreatedDate(oldUser.get().getCreatedDate())
                .build();
        int updated = usersDao.updateUser(newUser, oldUser.get().getVersion());
        if (updated != 1) {
            throw new CrmServiceApiStaleStateException(String.format("Concurrent modification. UserId: %s modified since request was made. Try again", userId));
        }
        log.info("action=updateUser result=success userId={} newUser={}", userId, newUser);
        return newUser;
    }
}
