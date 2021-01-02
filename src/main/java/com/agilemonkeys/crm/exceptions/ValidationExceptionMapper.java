package com.agilemonkeys.crm.exceptions;

import io.dropwizard.jersey.validation.JerseyViolationException;
import io.dropwizard.jersey.validation.JerseyViolationExceptionMapper;
import org.eclipse.jetty.http.HttpStatus;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<JerseyViolationException> {
    private JerseyViolationExceptionMapper delegate;

    public ValidationExceptionMapper(JerseyViolationExceptionMapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public Response toResponse(final JerseyViolationException exception) {
        Response defaultResponse = delegate.toResponse(exception);
        //OVERRIDE RESPONSE STATUS
        return Response.fromResponse(defaultResponse).status(HttpStatus.BAD_REQUEST_400).build();
    }
}
