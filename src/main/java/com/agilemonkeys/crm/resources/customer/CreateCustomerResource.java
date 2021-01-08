package com.agilemonkeys.crm.resources.customer;

import com.agilemonkeys.crm.domain.AuthenticatedUser;
import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.services.customer.CreateCustomerService;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path(CreateCustomerResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class CreateCustomerResource {

    public static final String PATH = "/crm/customers";

    private static final Logger log = LoggerFactory.getLogger(CreateCustomerResource.class);

    private final CreateCustomerService createCustomerService;

    public CreateCustomerResource(CreateCustomerService createCustomerService) {
        this.createCustomerService = createCustomerService;
    }

    @POST
    public Response createCustomer(@Valid CreateUpdateCustomerRequest request, @Auth AuthenticatedUser authInfo) {
        Customer createdCustomer = createCustomerService.createCustomer(authInfo.getUserId(), request);
        CreateCustomerResponse response = new CreateCustomerResponse(createdCustomer.getCustomerId());
        log.info("action=createCustomer result=success customerId={}", createdCustomer.getCustomerId());
        return Response.created(URI.create(GetCustomersResource.createResourcePath(response.getCustomerId()))).header(HttpHeaders.ETAG, createdCustomer.getVersion()).entity(response).build();
    }
}
