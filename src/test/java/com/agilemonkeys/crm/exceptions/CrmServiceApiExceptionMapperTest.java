package com.agilemonkeys.crm.exceptions;

import io.dropwizard.jersey.validation.ValidationErrorMessage;
import org.eclipse.jetty.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.Arrays;

public class CrmServiceApiExceptionMapperTest {

    private CrmServiceApiExceptionMapper underTest = new CrmServiceApiExceptionMapper();

    @Test
    public void should_return_a_response_with_404_and_error_message_when_CrmServiceApiNotFoundException() {
        CrmServiceApiNotFoundException exception = new CrmServiceApiNotFoundException("message");
        Response response = underTest.toResponse(exception);

        MatcherAssert.assertThat(response.getStatus(), Matchers.is(HttpStatus.NOT_FOUND_404));
        MatcherAssert.assertThat(response.getEntity(), Matchers.instanceOf(ValidationErrorMessage.class));
        ValidationErrorMessage error = (ValidationErrorMessage) response.getEntity();
        MatcherAssert.assertThat(error.getErrors(), Matchers.is(Arrays.asList("message")));
    }

    @Test
    public void should_return_a_response_with_412_and_error_message_when_CrmServiceApiStaleStateException() {
        CrmServiceApiStaleStateException exception = new CrmServiceApiStaleStateException("message");
        Response response = underTest.toResponse(exception);

        MatcherAssert.assertThat(response.getStatus(), Matchers.is(HttpStatus.PRECONDITION_FAILED_412));
        MatcherAssert.assertThat(response.getEntity(), Matchers.instanceOf(ValidationErrorMessage.class));
        ValidationErrorMessage error = (ValidationErrorMessage) response.getEntity();
        MatcherAssert.assertThat(error.getErrors(), Matchers.is(Arrays.asList("message")));
    }

    @Test
    public void should_return_a_response_with_409_and_error_message_when_CrmServiceApiDuplicatedException() {
        CrmServiceApiDuplicatedException exception = new CrmServiceApiDuplicatedException("message");
        Response response = underTest.toResponse(exception);

        MatcherAssert.assertThat(response.getStatus(), Matchers.is(HttpStatus.CONFLICT_409));
        MatcherAssert.assertThat(response.getEntity(), Matchers.instanceOf(ValidationErrorMessage.class));
        ValidationErrorMessage error = (ValidationErrorMessage) response.getEntity();
        MatcherAssert.assertThat(error.getErrors(), Matchers.is(Arrays.asList("message")));
    }

    @Test
    public void should_return_a_response_with_401_and_error_message_when_CrmServiceApiAuthException() {
        CrmServiceApiAuthException exception = new CrmServiceApiAuthException("message");
        Response response = underTest.toResponse(exception);

        MatcherAssert.assertThat(response.getStatus(), Matchers.is(HttpStatus.UNAUTHORIZED_401));
        MatcherAssert.assertThat(response.getEntity(), Matchers.instanceOf(ValidationErrorMessage.class));
        ValidationErrorMessage error = (ValidationErrorMessage) response.getEntity();
        MatcherAssert.assertThat(error.getErrors(), Matchers.is(Arrays.asList("message")));
    }
}