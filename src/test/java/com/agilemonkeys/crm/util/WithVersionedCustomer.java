package com.agilemonkeys.crm.util;

import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.customer.CustomerResponse;
import com.agilemonkeys.crm.resources.user.UserResponse;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import java.util.UUID;

public interface WithVersionedCustomer extends WithCustomer {

    public default Builder addVersion(Builder requestBuilder, Integer version) {
        requestBuilder.header(HttpHeaders.IF_MATCH, version);
        return requestBuilder;
    }

    public default VersionedCustomer getVersionedCustomer(UUID userId) {
        LoginResponse loginResponse = adminLogin();
        return getVersionedCustomer(loginResponse, userId);
    }

    public default VersionedCustomer getVersionedCustomer(LoginResponse loginResponse, UUID customerId) {
        Response httpResponse = getCustomerResponse(loginResponse, customerId);

        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(200));
        CustomerResponse response = httpResponse.readEntity(CustomerResponse.class);
        Integer version = Integer.valueOf(httpResponse.getHeaderString(HttpHeaders.ETAG));
        return new VersionedCustomer(response, version);
    }

    public static class VersionedCustomer {
        public final CustomerResponse customer;
        public final Integer version;

        public VersionedCustomer(CustomerResponse customer, Integer version) {
            this.customer = customer;
            this.version = version;
        }
    }
}
