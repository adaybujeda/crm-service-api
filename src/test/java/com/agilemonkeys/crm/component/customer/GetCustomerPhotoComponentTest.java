package com.agilemonkeys.crm.component.customer;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.customer.GetCustomerPhotoResource;
import com.agilemonkeys.crm.util.WithCustomer;
import com.agilemonkeys.crm.util.WithPhoto;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.UUID;

public class GetCustomerPhotoComponentTest extends RunningServiceBaseTest implements WithPhoto, WithCustomer {

    @Test
    public void should_return_401_when_not_authorized() {
        String url = getResourceUrl(GetCustomerPhotoResource.createResourcePath(UUID.randomUUID()));
        Response httpResponse = getClient().target(url).request().get();

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void should_return_404_when_customer_not_found() {
        String url = GetCustomerPhotoResource.createResourcePath(UUID.randomUUID());
        Response httpResponse = authorizedRequest(url).get();

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(404));
        httpResponse.close();
    }

    @Test
    public void should_return_200_when_successful() throws Exception {
        LoginResponse loginResponse = adminLogin();
        UUID customerId = createCustomer(loginResponse);
        createPhoto(loginResponse, customerId);

        Response httpResponse = authorizedRequest(GetCustomerPhotoResource.createResourcePath(customerId)).get();

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(200));
        MatcherAssert.assertThat(httpResponse.getMediaType(), Matchers.is(TYPE));
        httpResponse.close();
    }
}
