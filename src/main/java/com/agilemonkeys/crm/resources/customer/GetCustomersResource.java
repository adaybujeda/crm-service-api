package com.agilemonkeys.crm.resources.customer;

import com.agilemonkeys.crm.domain.Customer;
import com.agilemonkeys.crm.resources.customer.CustomerResponse.CustomerResponseCollection;
import com.agilemonkeys.crm.services.customer.GetCustomersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path(GetCustomersResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@PermitAll
public class GetCustomersResource {

    public static final String PATH = "/crm/customers";

    private static final Logger log = LoggerFactory.getLogger(GetCustomersResource.class);

    private final GetCustomersService getCustomersService;

    public GetCustomersResource(GetCustomersService getCustomersService) {
        this.getCustomersService = getCustomersService;
    }

    @GET
    public CustomerResponseCollection getCustomerList() {
        List<CustomerResponse> responseItems = getCustomersService.getAllCustomers().stream().map(customer -> CustomerResponse.from(customer)).collect(Collectors.toList());
        log.info("action=getCustomerList result=success responseItems={}", responseItems.size());
        return new CustomerResponseCollection(responseItems);
    }

    @GET
    @Path("/{customerId}")
    public Response getCustomer(@PathParam("customerId") UUID customerId) {
        Customer customer = getCustomersService.getCustomerById(customerId);
        CustomerResponse response = CustomerResponse.from(customer);
        log.info("action=getCustomer result=success customerId={}", customerId);
        return Response.ok(response).header(HttpHeaders.ETAG, customer.getVersion()).build();
    }

    public static String createResourcePath(UUID customerId) {
        return String.format("%s/%s", PATH, customerId);
    }
}
