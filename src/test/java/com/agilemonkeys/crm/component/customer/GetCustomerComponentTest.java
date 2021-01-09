package com.agilemonkeys.crm.component.customer;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.customer.CustomerResponse.CustomerResponseCollection;
import com.agilemonkeys.crm.resources.customer.GetCustomersResource;
import com.agilemonkeys.crm.util.WithVersionedCustomer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.UUID;

public class GetCustomerComponentTest extends RunningServiceBaseTest implements WithVersionedCustomer {

    @Test
    public void getCustomer_should_return_401_when_not_authorized() {
        String url = getResourceUrl(GetCustomersResource.createResourcePath(UUID.randomUUID()));
        Response httpResponse = getClient().target(url).request().get();

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void getCustomer_should_return_404_when_customer_not_found() {
        String url = GetCustomersResource.createResourcePath(UUID.randomUUID());
        Response httpResponse = authorizedRequest(url).get();

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(404));
        httpResponse.close();
    }

    @Test
    public void getCustomer_should_return_200_when_successful() {
        LoginResponse loginResponse = adminLogin();
        UUID customerId = createCustomer(loginResponse);
        VersionedCustomer versionedCustomer = getVersionedCustomer(loginResponse, customerId);
        MatcherAssert.assertThat(versionedCustomer.customer.getCustomerId(), Matchers.is(customerId));
        MatcherAssert.assertThat(versionedCustomer.customer.getProvidedId(), Matchers.notNullValue());
        MatcherAssert.assertThat(versionedCustomer.customer.getName(), Matchers.notNullValue());
        MatcherAssert.assertThat(versionedCustomer.customer.getSurname(), Matchers.notNullValue());
        MatcherAssert.assertThat(versionedCustomer.customer.getPhotoUrl(), Matchers.nullValue());
        MatcherAssert.assertThat(versionedCustomer.customer.getCreatedBy(), Matchers.is(loginResponse.getUserId()));
        MatcherAssert.assertThat(versionedCustomer.customer.getCreatedDate(), Matchers.notNullValue());
        MatcherAssert.assertThat(versionedCustomer.customer.getUpdatedBy(), Matchers.is(loginResponse.getUserId()));
        MatcherAssert.assertThat(versionedCustomer.customer.getUpdatedDate(), Matchers.notNullValue());
        MatcherAssert.assertThat(versionedCustomer.version, Matchers.is(1));
    }

    @Test
    public void getAllCustomers_should_return_401_when_not_authorized() {
        String url = getResourceUrl(GetCustomersResource.PATH);
        Response httpResponse = getClient().target(url).request().get();

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void getAllCustomers_should_return_200_when_successful() {
        LoginResponse loginResponse = adminLogin();
        UUID customerId = createCustomer(loginResponse);

        Response httpResponse = authorizedRequest(loginResponse, GetCustomersResource.PATH).get();

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(200));
        CustomerResponseCollection result = httpResponse.readEntity(CustomerResponseCollection.class);

        MatcherAssert.assertThat(result.getItems().isEmpty(), Matchers.is(false));
    }
}