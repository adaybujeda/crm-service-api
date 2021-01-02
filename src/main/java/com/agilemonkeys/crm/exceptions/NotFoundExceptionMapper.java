package com.agilemonkeys.crm.exceptions;

import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<CrmServiceApiNotFoundException> {

    private static final Logger log = LoggerFactory.getLogger(NotFoundExceptionMapper.class);

    public Response toResponse(CrmServiceApiNotFoundException exception) {
        log.debug("action=notFoundException message={}", exception.getMessage(), exception);
        return Response.status(HttpStatus.NOT_FOUND_404).entity(new ErrorResponse(exception.getMessage())).build();
    }
}
