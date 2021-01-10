package com.agilemonkeys.crm.resources.customer;

import com.agilemonkeys.crm.domain.AuthenticatedUser;
import com.agilemonkeys.crm.domain.CustomerPhoto;
import com.agilemonkeys.crm.services.customer.UploadPhotoService;
import com.google.common.io.ByteStreams;
import io.dropwizard.auth.Auth;
import io.dropwizard.jersey.validation.ValidationErrorMessage;
import org.eclipse.jetty.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.security.PermitAll;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;


@Path(UploadCustomerPhotoResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes({"image/jpeg", "image/png"})
@PermitAll
public class UploadCustomerPhotoResource {
    public static final String PATH = "/crm/customers/{customerId}/photo";
    private static final int MAX_SIZE_IN_MB = 1;
    private static final int MAX_SIZE_IN_BYTES = 1024 * 1024 * MAX_SIZE_IN_MB;

    private static final Logger log = LoggerFactory.getLogger(UploadCustomerPhotoResource.class);

    private final UploadPhotoService uploadPhotoService;

    public UploadCustomerPhotoResource(UploadPhotoService uploadPhotoService) {
        this.uploadPhotoService = uploadPhotoService;
    }

    @POST
    public Response uploadPhoto(@PathParam("customerId") @NotNull UUID customerId, @NotNull @HeaderParam(HttpHeaders.IF_MATCH) Integer version, @Auth AuthenticatedUser authInfo,
                                @HeaderParam(HttpHeaders.CONTENT_TYPE) @NotNull String fileType, @HeaderParam(HttpHeaders.CONTENT_LENGTH) Long fileSize, InputStream fileStream) throws IOException {

        if (fileSize.equals(0l)) {
            log.warn("action=uploadPhoto result=no-data customerId={} fileSize={}", customerId, fileSize);
            return Response.status(HttpStatus.BAD_REQUEST_400).entity(new ValidationErrorMessage(Arrays.asList("no-content sent"))).build();
        }

        if (fileSize > MAX_SIZE_IN_BYTES) {
            log.warn("action=uploadPhoto result=photo-too-large customerId={} fileSize={}", customerId, fileSize);
            return Response.status(HttpStatus.PAYLOAD_TOO_LARGE_413).entity(new ValidationErrorMessage(Arrays.asList(String.format("File too big. Max size: %sMB", MAX_SIZE_IN_MB)))).build();
        }

        byte[] photoBytes = ByteStreams.toByteArray(ByteStreams.limit(fileStream, fileSize));
        CustomerPhoto customerPhoto = uploadPhotoService.createPhotoForCustomer(customerId, version, fileType, photoBytes, authInfo.getUserId());
        log.info("action=uploadPhoto result=success customerId={} customerPhoto={}", customerId, customerPhoto);
        String getPhotoPath = createResourcePath(customerPhoto.getCustomerId());
        return Response.status(Response.Status.CREATED).location(URI.create(getPhotoPath)).build();
    }

    public static String createResourcePath(UUID customerId) {
        return UriBuilder.fromResource(UploadCustomerPhotoResource.class)
                .resolveTemplate("customerId", customerId).build().toString();
    }
}
