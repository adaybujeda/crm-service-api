package com.agilemonkeys.crm.exceptions;

import org.eclipse.jetty.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

public class NotFoundExceptionMapperTest {

    private NotFoundExceptionMapper underTest = new NotFoundExceptionMapper();

    @Test
    public void should_return_a_response_with_404_and_error_message() {
        CrmServiceApiNotFoundException exception = new CrmServiceApiNotFoundException("message");
        Response response = underTest.toResponse(exception);

        MatcherAssert.assertThat(response.getStatus(), Matchers.is(HttpStatus.NOT_FOUND_404));
        MatcherAssert.assertThat(response.getEntity(), Matchers.is(new ErrorResponse("message")));
    }

}