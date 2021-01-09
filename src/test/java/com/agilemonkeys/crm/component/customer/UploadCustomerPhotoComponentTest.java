package com.agilemonkeys.crm.component.customer;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.customer.GetCustomerPhotoResource;
import com.agilemonkeys.crm.resources.customer.UploadCustomerPhotoResource;
import com.agilemonkeys.crm.util.WithPhoto;
import com.agilemonkeys.crm.util.WithVersionedCustomer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.UUID;

public class UploadCustomerPhotoComponentTest extends RunningServiceBaseTest implements WithPhoto, WithVersionedCustomer {

    @Test
    public void should_return_401_when_not_authorized() {
        String url = getResourceUrl(UploadCustomerPhotoResource.createResourcePath(UUID.randomUUID()));
        Response httpResponse = getClient().target(url).request().post(Entity.entity(new byte[10], TYPE));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(401));
        httpResponse.close();
    }

    @Test
    public void should_return_400_when_invalid_request() {
        String url = UploadCustomerPhotoResource.createResourcePath(UUID.randomUUID());
        byte[] photo = new byte[0];
        Response httpResponse = authorizedRequest(url).header(HttpHeaders.IF_MATCH, 1).post(Entity.entity(photo, TYPE));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(400));
        httpResponse.close();
    }

    @Test
    public void should_return_404_when_customer_not_found() {
        String url = UploadCustomerPhotoResource.createResourcePath(UUID.randomUUID());
        Response httpResponse = authorizedRequest(url).header(HttpHeaders.IF_MATCH, 1).post(Entity.entity(new byte[10], TYPE));

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(404));
        httpResponse.close();
    }

    @Test
    public void should_return_412_when_version_does_not_match() throws Exception {
        LoginResponse loginResponse = adminLogin();
        UUID customerId = createCustomer(loginResponse);
        Response photoResponse = createPhotoResponse(loginResponse, customerId, 10);
        MatcherAssert.assertThat(photoResponse.getStatus(), Matchers.is(412));
        photoResponse.close();
    }

    @Test
    public void should_return_201_when_successful() throws Exception {
        LoginResponse loginResponse = adminLogin();
        UUID customerId = createCustomer(loginResponse);
        createPhoto(loginResponse, customerId);

        VersionedCustomer versionedCustomer = getVersionedCustomer(customerId);
        MatcherAssert.assertThat(versionedCustomer.customer.getPhotoUrl(), Matchers.is(GetCustomerPhotoResource.createResourcePath(customerId)));
        MatcherAssert.assertThat(versionedCustomer.version, Matchers.is(2));
    }

    @Test
    public void should_allow_multiple_upload() throws Exception {
        LoginResponse loginResponse = adminLogin();
        UUID customerId = createCustomer(loginResponse);

        createPhoto(loginResponse, customerId, 1);
        createPhoto(loginResponse, customerId, 2);
        createPhoto(loginResponse, customerId, 3);
    }
}
