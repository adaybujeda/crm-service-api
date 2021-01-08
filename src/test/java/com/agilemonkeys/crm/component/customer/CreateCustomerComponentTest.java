package com.agilemonkeys.crm.component.customer;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.customer.CreateCustomerResource;
import com.agilemonkeys.crm.resources.customer.CreateCustomerResponse;
import com.agilemonkeys.crm.resources.customer.CreateUpdateCustomerRequest;
import com.agilemonkeys.crm.util.WithCustomer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.Optional;

public class CreateCustomerComponentTest extends RunningServiceBaseTest implements WithCustomer {

    @Test
    public void should_return_401_when_not_authorized() {
        CreateUpdateCustomerRequest request = createRandomValidRequest(Optional.empty());
        String url = getResourceUrl(CreateCustomerResource.PATH);
        Response httpResponse = getClient().target(url).request().post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void should_return_400_when_invalid_request() {
        CreateUpdateCustomerRequest request = new CreateUpdateCustomerRequest("name", null, null);
        Response httpResponse = authorizedRequest(CreateCustomerResource.PATH).post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(400));
        httpResponse.close();
    }

    @Test
    public void should_return_409_when_username_is_already_used() {
        CreateUpdateCustomerRequest request = createRandomValidRequest(Optional.empty());
        CreateCustomerResponse newCustomer = authorizedRequest(CreateCustomerResource.PATH).post(Entity.json(request)).readEntity(CreateCustomerResponse.class);

        Response httpResponse = authorizedRequest(CreateCustomerResource.PATH).post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(409));
        httpResponse.close();
    }

    @Test
    public void should_return_userId_for_successful_request() {
        LoginResponse loginResponse = adminLogin();
        createCustomer(loginResponse);
    }
}
