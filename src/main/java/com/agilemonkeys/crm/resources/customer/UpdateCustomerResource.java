package com.agilemonkeys.crm.resources.customer;

import com.agilemonkeys.crm.domain.AuthenticatedUser;
import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.services.customer.UpdateCustomerService;
import io.dropwizard.auth.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path(UpdateCustomerResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class UpdateCustomerResource {

    public static final String PATH = "/crm/customers";

    private static final Logger log = LoggerFactory.getLogger(UpdateCustomerResource.class);

    private final UpdateCustomerService updateCustomerService;

    public UpdateCustomerResource(UpdateCustomerService updateCustomerService) {
        this.updateCustomerService = updateCustomerService;
    }

    @PUT
    @Path("/{customerId}")
    public Response updateCustomer(@PathParam("customerId") UUID customerId, @NotNull @HeaderParam(HttpHeaders.IF_MATCH) Integer version, @Valid CreateUpdateCustomerRequest request, @Auth AuthenticatedUser authInfo) {
        Customer updatedCustomer = updateCustomerService.updateCustomer(customerId, version, request, authInfo.getUserId());
        log.info("action=updateCustomer result=success customerId={}", updatedCustomer.getCustomerId());
        return Response.noContent().build();
    }

    public static String createResourcePath(UUID customerId) {
        return String.format("%s/%s", PATH, customerId);
    }
}
