package com.agilemonkeys.crm.services.auth;

import com.agilemonkeys.crm.domain.CrmAuthToken;
import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.exceptions.CrmServiceApiAuthException;
import com.agilemonkeys.crm.resources.auth.LoginRequest;
import com.agilemonkeys.crm.services.user.GetUsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final GetUsersService getUsersService;
    private final JwtTokenFactory jwtTokenFactory;
    private final CrmPasswordHashService crmPasswordHashService;

    public LoginService(GetUsersService getUsersService, JwtTokenFactory jwtTokenFactory, CrmPasswordHashService crmPasswordHashService) {
        this.getUsersService = getUsersService;
        this.jwtTokenFactory = jwtTokenFactory;
        this.crmPasswordHashService = crmPasswordHashService;
    }

    public CrmAuthToken login(LoginRequest loginRequest) {
        Optional<User> user = getUsersService.getUserByUsername(loginRequest.getUsername());
        if (!user.isPresent()) {
            log.warn("action=login result=username-not-found username={}", loginRequest.getUsername());
            throw new CrmServiceApiAuthException("Invalid credentials");
        }

        if (user.get().isDeleted()) {
            log.warn("action=login result=deleted-user username={}", loginRequest.getUsername());
            throw new CrmServiceApiAuthException("Invalid credentials");
        }

        boolean isPasswordOK = crmPasswordHashService.checkPassword(loginRequest.getPassword(), user.get().getPasswordHash());

        if (!isPasswordOK) {
            log.warn("action=login result=invalid-password user={}", user.get());
            throw new CrmServiceApiAuthException("Invalid credentials");
        }

        CrmAuthToken authToken = jwtTokenFactory.createToken(user.get().getUserId(), user.get().getRole());
        log.info("action=login result=success userId={} authToken={}", user.get().getUserId(), authToken);
        return authToken;
    }
}
