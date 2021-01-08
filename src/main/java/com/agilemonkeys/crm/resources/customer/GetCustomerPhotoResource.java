package com.agilemonkeys.crm.resources.customer;

import com.agilemonkeys.crm.domain.CustomerPhoto;
import com.agilemonkeys.crm.services.customer.GetCustomerPhotoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.UUID;


@Path(GetCustomerPhotoResource.PATH)
@PermitAll
public class GetCustomerPhotoResource {
    public static final String PATH = "/crm/customers/{customerId}/photo";

    private static final Logger log = LoggerFactory.getLogger(GetCustomerPhotoResource.class);

    private final GetCustomerPhotoService customerPhotoService;

    public GetCustomerPhotoResource(GetCustomerPhotoService customerPhotoService) {
        this.customerPhotoService = customerPhotoService;
    }

    @GET
    public Response getImage(@PathParam("customerId") UUID customerId) {
        CustomerPhoto customerPhoto = customerPhotoService.getCustomerPhoto(customerId);
        log.info("action=getImage result=success customerId={} customerPhoto={}", customerId, customerPhoto);
        return Response.ok(customerPhoto.getPhoto()).type(customerPhoto.getContentType()).build();
    }
}
