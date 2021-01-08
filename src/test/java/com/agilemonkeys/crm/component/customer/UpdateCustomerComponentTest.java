package com.agilemonkeys.crm.component.customer;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.customer.CreateUpdateCustomerRequest;
import com.agilemonkeys.crm.resources.customer.UpdateCustomerResource;
import com.agilemonkeys.crm.util.WithVersionedCustomer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.UUID;

public class UpdateCustomerComponentTest extends RunningServiceBaseTest implements WithVersionedCustomer {

    @Test
    public void should_return_401_when_not_authorized() {
        String url = getResourceUrl(UpdateCustomerResource.createResourcePath(UUID.randomUUID()));
        CreateUpdateCustomerRequest updateRequest = createRandomValidRequest(Optional.empty());
        Response httpResponse = getClient().target(url).request().put(Entity.json(updateRequest));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void should_return_400_when_invalid_request() {
        CreateUpdateCustomerRequest request = new CreateUpdateCustomerRequest("name", null, null);
        String url = UpdateCustomerResource.createResourcePath(UUID.randomUUID());
        Response httpResponse = authorizedRequest(url).put(Entity.json(request));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(400));
        httpResponse.close();
    }

    @Test
    public void should_return_404_when_customer_not_found() {
        CreateUpdateCustomerRequest updateRequest = createRandomValidRequest(Optional.empty());
        String url = UpdateCustomerResource.createResourcePath(UUID.randomUUID());
        Response httpResponse = authorizedRequest(url).header(HttpHeaders.IF_MATCH, 1).put(Entity.json(updateRequest));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(404));
        httpResponse.close();
    }

    @Test
    public void should_return_412_when_version_does_not_match() {
        LoginResponse loginResponse = adminLogin();
        UUID customerId = createCustomer(loginResponse);

        CreateUpdateCustomerRequest updateRequest = createRandomValidRequest(Optional.empty());
        String url = UpdateCustomerResource.createResourcePath(customerId);
        Response httpResponse = authorizedRequest(url).header(HttpHeaders.IF_MATCH, 10).put(Entity.json(updateRequest));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(412));
        httpResponse.close();
    }

    @Test
    public void should_return_409_when_providedId_is_already_used() {
        LoginResponse loginResponse = adminLogin();
        UUID customerId = createCustomer(loginResponse);
        VersionedCustomer versionedCustomer = getVersionedCustomer(loginResponse, customerId);
        MatcherAssert.assertThat(versionedCustomer.customer.getCustomerId(), Matchers.is(customerId));
        String usedProvidedId = versionedCustomer.customer.getProvidedId();


        UUID customerToUpdate = createCustomer(loginResponse);
        CreateUpdateCustomerRequest updateRequest = createRandomValidRequest(Optional.of(usedProvidedId));
        String url = UpdateCustomerResource.createResourcePath(customerToUpdate);
        Response httpResponse = authorizedRequest(url).header(HttpHeaders.IF_MATCH, versionedCustomer.version).put(Entity.json(updateRequest));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(409));
        httpResponse.close();
    }

    @Test
    public void should_return_204_when_successful() {
        LoginResponse loginResponse = adminLogin();
        UUID customerId = createCustomer(loginResponse);
        VersionedCustomer versionedCustomer = getVersionedCustomer(loginResponse, customerId);
        MatcherAssert.assertThat(versionedCustomer.customer.getCustomerId(), Matchers.is(customerId));

        CreateUpdateCustomerRequest updateRequest = new CreateUpdateCustomerRequest(versionedCustomer.customer.getProvidedId(),"newName", "newSurname");
        String url = UpdateCustomerResource.createResourcePath(versionedCustomer.customer.getCustomerId());
        Response httpResponse = authorizedRequest(url).header(HttpHeaders.IF_MATCH, versionedCustomer.version).put(Entity.json(updateRequest));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(204));
        httpResponse.close();
    }
}
