package com.agilemonkeys.crm.component.customer;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.customer.DeleteCustomerResource;
import com.agilemonkeys.crm.util.WithVersionedCustomer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.UUID;

public class DeleteCustomerComponentTest extends RunningServiceBaseTest implements WithVersionedCustomer {

    @Test
    public void should_return_401_when_not_authorized() {
        String url = getResourceUrl(DeleteCustomerResource.createResourcePath(UUID.randomUUID()));
        Response httpResponse = getClient().target(url).request().delete();

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void should_return_204_when_successful() {
        LoginResponse loginResponse = adminLogin();
        UUID customerId = createCustomer(loginResponse);
        VersionedCustomer versionedCustomer = getVersionedCustomer(loginResponse, customerId);
        MatcherAssert.assertThat(versionedCustomer.customer.getCustomerId(), Matchers.is(customerId));

        String path = DeleteCustomerResource.createResourcePath(customerId);
        Response deleteResponse = authorizedRequest(loginResponse, path).delete();

        MatcherAssert.assertThat(deleteResponse.getStatus(), Matchers.is(204));
        deleteResponse.close();

        Response getResponseAfterDelete = getCustomerResponse(loginResponse, customerId);
        MatcherAssert.assertThat(getResponseAfterDelete.getStatus(), Matchers.is(404));
    }
}
