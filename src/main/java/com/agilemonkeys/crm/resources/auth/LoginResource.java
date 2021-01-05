package com.agilemonkeys.crm.resources.auth;

import com.agilemonkeys.crm.domain.CrmAuthToken;
import com.agilemonkeys.crm.services.auth.LoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(LoginResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoginResource {

    public static final String PATH = "/auth/login";

    private static final Logger log = LoggerFactory.getLogger(LoginResource.class);

    private final LoginService loginService;

    public LoginResource(LoginService loginService) {
        this.loginService = loginService;
    }

    @POST
    public LoginResponse login(@Valid LoginRequest request) {
        CrmAuthToken authToken = loginService.login(request);
        LoginResponse response = new LoginResponse(authToken.getUserId(), authToken.getEncodedJwtToken(), authToken.getTokenTTLInSeconds());
        log.info("action=login result=success authToken={}", authToken);
        return response;
    }
}
