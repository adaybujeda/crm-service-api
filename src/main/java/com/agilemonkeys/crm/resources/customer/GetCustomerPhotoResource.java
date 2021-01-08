package com.agilemonkeys.crm.resources.customer;

import com.agilemonkeys.crm.domain.CustomerPhoto;
import com.agilemonkeys.crm.storage.CustomerPhotosDao;
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

    private final CustomerPhotosDao customerPhotosDao;

    public GetCustomerPhotoResource(CustomerPhotosDao customerPhotosDao) {
        this.customerPhotosDao = customerPhotosDao;
    }

    @GET
    public Response getImage(@PathParam("customerId") UUID customerId) {
        CustomerPhoto customerPhoto = customerPhotosDao.getCustomerPhotoById(customerId).get();
        log.info("action=getImage result=success customerPhoto={}", customerPhoto);
        return Response.ok(customerPhoto.getPhoto()).type(customerPhoto.getContentType()).build();
    }
}
