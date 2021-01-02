package com.agilemonkeys.crm.exceptions;

import io.dropwizard.jersey.validation.JerseyViolationException;
import io.dropwizard.jersey.validation.JerseyViolationExceptionMapper;
import org.eclipse.jetty.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.core.Response;

public class ValidationExceptionMapperTest {

    private JerseyViolationExceptionMapper jerseyValidatorExceptionMapper = Mockito.mock(JerseyViolationExceptionMapper.class);
    private ValidationExceptionMapper underTest = new ValidationExceptionMapper(jerseyValidatorExceptionMapper);

    @Test
    public void should_override_response_code_to_400() {
        JerseyViolationException validationException = Mockito.mock(JerseyViolationException.class);
        Response defaultResponse = Response.status(HttpStatus.PRECONDITION_FAILED_412).build();
        Mockito.when(jerseyValidatorExceptionMapper.toResponse(validationException)).thenReturn(defaultResponse);
        Response response = underTest.toResponse(validationException);

        MatcherAssert.assertThat(response.getStatus(), Matchers.is(HttpStatus.BAD_REQUEST_400));
    }

}