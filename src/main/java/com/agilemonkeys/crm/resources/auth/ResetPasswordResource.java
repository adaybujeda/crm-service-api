package com.agilemonkeys.crm.resources.auth;

import com.agilemonkeys.crm.domain.AuthenticatedUser;
import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.services.auth.ResetPasswordService;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path(ResetPasswordResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class ResetPasswordResource {

    public static final String PATH = "/auth/reset-password";

    private static final Logger log = LoggerFactory.getLogger(ResetPasswordResource.class);

    private final ResetPasswordService resetPasswordService;

    public ResetPasswordResource(ResetPasswordService resetPasswordService) {
        this.resetPasswordService = resetPasswordService;
    }

    @POST
    public Response resetPassword(@Valid ResetPasswordRequest request, @Auth AuthenticatedUser authUser) {
        User user = resetPasswordService.resetPassword(authUser.getUserId(), request.getNewPassword());
        log.info("action=resetPassword result=success user={}", user);
        return Response.noContent().build();
    }
}
