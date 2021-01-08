package com.agilemonkeys.crm.resources.customer;

import com.agilemonkeys.crm.domain.UserRole;
import com.agilemonkeys.crm.services.customer.DeleteCustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;

@Path(DeleteCustomerResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class DeleteCustomerResource {

    public static final String PATH = "/crm/customers";

    private static final Logger log = LoggerFactory.getLogger(DeleteCustomerResource.class);

    private DeleteCustomerService deleteCustomerService;

    public DeleteCustomerResource(DeleteCustomerService deleteCustomerService) {
        this.deleteCustomerService = deleteCustomerService;
    }

    @DELETE
    @Path("/{customerId}")
    public Response deleteCustomer(@PathParam("customerId") UUID customerId) {
        deleteCustomerService.deleteCustomer(customerId);
        log.info("action=deleteCustomer result=success customerId={}", customerId);
        return Response.noContent().build();
    }
}
