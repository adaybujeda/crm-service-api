package com.agilemonkeys.crm.util;

import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.customer.CreateCustomerResource;
import com.agilemonkeys.crm.resources.customer.CreateCustomerResponse;
import com.agilemonkeys.crm.resources.customer.CreateUpdateCustomerRequest;
import com.agilemonkeys.crm.resources.customer.GetCustomersResource;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.UUID;

public interface WithCustomer extends WithAuth {

    public default UUID createCustomer(LoginResponse loginResponse) {
        CreateUpdateCustomerRequest request = createRandomValidRequest(Optional.empty());
        Response httpResponse = authorizedRequest(loginResponse, CreateCustomerResource.PATH).post(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(201));
        MatcherAssert.assertThat(httpResponse.getHeaderString(HttpHeaders.ETAG), Matchers.notNullValue());

        CreateCustomerResponse response = httpResponse.readEntity(CreateCustomerResponse.class);
        MatcherAssert.assertThat(httpResponse.getHeaderString(HttpHeaders.LOCATION), Matchers.notNullValue());
        MatcherAssert.assertThat(httpResponse.getHeaderString(HttpHeaders.LOCATION), Matchers.containsString(response.getCustomerId().toString()));
        MatcherAssert.assertThat(response.getCustomerId(), Matchers.notNullValue());
        return response.getCustomerId();
    }

    public default CreateUpdateCustomerRequest createRandomValidRequest(Optional<String> providedId) {
        CreateUpdateCustomerRequest request = new CreateUpdateCustomerRequest(
                providedId.orElse(UUID.randomUUID().toString()),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString());

        return request;
    }

    public default Response getCustomerResponse(UUID customerId) {
        LoginResponse loginResponse = adminLogin();
        return getCustomerResponse(loginResponse, customerId);
    }

    public default Response getCustomerResponse(LoginResponse loginResponse, UUID customerId) {
        String path = GetCustomersResource.createResourcePath(customerId);
        Response httpResponse = authorizedRequest(loginResponse, path).get();

        return httpResponse;
    }
}
