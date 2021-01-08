package com.agilemonkeys.crm.exceptions;

import io.dropwizard.jersey.validation.ValidationErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.List;

@Provider
public class CrmServiceApiExceptionMapper implements ExceptionMapper<CrmServiceApiException> {

    private static final Logger log = LoggerFactory.getLogger(CrmServiceApiExceptionMapper.class);

    @Override
    public Response toResponse(final CrmServiceApiException exception) {
        log.debug("action={} message={}", exception.getClass().getSimpleName(), exception.getMessage(), exception);
        List<String> errors = Arrays.asList(exception.getMessage());
        return Response.status(exception.getHttpStatusCode()).type(MediaType.APPLICATION_JSON_TYPE).entity(new ValidationErrorMessage(errors)).build();
    }
}
