package com.agilemonkeys.crm.acceptance;

import com.agilemonkeys.crm.RunningServiceBaseTest;
import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.customer.CreateUpdateCustomerRequest;
import com.agilemonkeys.crm.resources.customer.DeleteCustomerResource;
import com.agilemonkeys.crm.resources.customer.GetCustomerPhotoResource;
import com.agilemonkeys.crm.resources.customer.UpdateCustomerResource;
import com.agilemonkeys.crm.resources.user.CreateUserRequest;
import com.agilemonkeys.crm.resources.user.CreateUserResource;
import com.agilemonkeys.crm.resources.user.CreateUserResponse;
import com.agilemonkeys.crm.util.WithAuth;
import com.agilemonkeys.crm.util.WithPhoto;
import com.agilemonkeys.crm.util.WithVersionedCustomer;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.UUID;

public class CustomersApiAcceptanceTest extends RunningServiceBaseTest implements WithVersionedCustomer, WithPhoto {

    private static final String USER_USERNAME = UUID.randomUUID().toString();
    private static final String USER_PASSWORD = "P4ssword!";

    @Test
    public void user_api_acceptance_test() throws Exception {
        //CREATE NEW USER
        LoginResponse rootAuthInfo = login(WithAuth.ADMIN_USERNAME, WithAuth.ADMIN_PASSWORD);
        //CREATE REGULAR USER
        UUID regularUser = createUser(rootAuthInfo, USER_USERNAME, USER_PASSWORD, UserRole.USER);
        //VERIFY USER CAN LOG IN
        LoginResponse loginResponse = login(USER_USERNAME, USER_PASSWORD);
        MatcherAssert.assertThat(loginResponse.getUserId(), Matchers.is(regularUser));

        //CREATE CUSTOMER + PHOTO
        UUID customerId = createCustomer(loginResponse);
        createPhoto(loginResponse, customerId);
        VersionedCustomer versionedCustomer = getVersionedCustomer(loginResponse, customerId);

        //UPDATE CUSTOMER
        CreateUpdateCustomerRequest updateRequest = new CreateUpdateCustomerRequest(versionedCustomer.customer.getProvidedId(),"newName", "newSurname");
        String url = UpdateCustomerResource.createResourcePath(customerId);
        Response httpResponse = authorizedRequest(url).header(HttpHeaders.IF_MATCH, versionedCustomer.version).put(Entity.json(updateRequest));
        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(204));
        httpResponse.close();

        //UPDATE PHOTO
        versionedCustomer = getVersionedCustomer(loginResponse, customerId);
        createPhoto(loginResponse, customerId, versionedCustomer.version);

        //DELETE CUSTOMER
        String deleteUserPath = DeleteCustomerResource.createResourcePath(customerId);
        Response deleteUserResponse = authorizedRequest(loginResponse, deleteUserPath).delete();
        MatcherAssert.assertThat(deleteUserResponse.getStatus(), Matchers.is(204));
        deleteUserResponse.close();

        //ENSURE DATA IS DELETED
        Response customerResponse = getCustomerResponse(loginResponse, customerId);
        MatcherAssert.assertThat(customerResponse.getStatus(), Matchers.is(404));
        customerResponse.close();
        Response photoResponse = authorizedRequest(loginResponse, GetCustomerPhotoResource.createResourcePath(customerId)).get();
        MatcherAssert.assertThat(photoResponse.getStatus(), Matchers.is(404));
        photoResponse.close();
    }

    private UUID createUser(LoginResponse authInfo, String username, String password, UserRole role) {
        CreateUserRequest adminUserRequest = new CreateUserRequest(UUID.randomUUID().toString(), username, password, role);
        CreateUserResponse createUserResponse = authorizedRequest(authInfo, CreateUserResource.PATH).post(Entity.json(adminUserRequest)).readEntity(CreateUserResponse.class);
       return createUserResponse.getUserId();
    }
}
