package com.agilemonkeys.crm.services.user;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.exceptions.CrmServiceApiNotFoundException;
import com.agilemonkeys.crm.storage.UsersDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GetUsersService {

    private static final Logger log = LoggerFactory.getLogger(GetUsersService.class);

    private final UsersDao usersDao;

    public GetUsersService(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    public User getUserById(UUID userId) {
        Optional<User> user = usersDao.getUserById(userId);
        if(!user.isPresent()) {
            throw new CrmServiceApiNotFoundException(String.format("UserId: %s not found", userId));
        }
        log.info("action=getUserById result=success userId={} user={}", userId, user.get());
        return user.get();
    }

    public Optional<User> getUserByUsername(String username) {
        if(username == null || username.isEmpty()) {
            return Optional.empty();
        }

        String normalizedUsername = username.trim().toLowerCase();
        Optional<User> user = usersDao.getUserByUsername(normalizedUsername);
        log.info("action=getUserByUsername result=success username={} user={}", username, user.orElse(null));
        return user;
    }

    public List<User> getAllUsers() {
        List<User> users = usersDao.getUsers();
        log.info("action=getAllUsers result=success items={}", users.size());
        return users;
    }
}
