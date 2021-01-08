package com.agilemonkeys.crm.resources;

import com.agilemonkeys.crm.domain.AuthenticatedUser;
import org.slf4j.MDC;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class CrmServiceLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Override
    public void filter (ContainerRequestContext containerRequestContext) throws IOException {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) containerRequestContext.getSecurityContext().getUserPrincipal();
        if(authenticatedUser != null) {
            MDC.put("userId", authenticatedUser.getUserId().toString());
        }
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {
        //RESET CONTEXT
        MDC.clear();
    }
}
