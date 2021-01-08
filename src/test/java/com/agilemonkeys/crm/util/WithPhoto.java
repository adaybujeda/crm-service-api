package com.agilemonkeys.crm.util;

import com.agilemonkeys.crm.resources.auth.LoginResponse;
import com.agilemonkeys.crm.resources.customer.UploadCustomerPhotoResource;
import io.dropwizard.util.Resources;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.net.URI;
import java.util.UUID;

public interface WithPhoto extends WithAuth{
    public static final MediaType TYPE = MediaType.valueOf("image/png");
    public static final String SAMPLE_FILE = "images/sample.png";

    public default void createPhoto(LoginResponse loginResponse, UUID customerId) throws Exception {
        createPhoto(loginResponse, customerId, 1);
    }

    public default void createPhoto(LoginResponse loginResponse, UUID customerId, Integer version) throws Exception {
        Response httpResponse = createPhotoResponse(loginResponse, customerId, version);
        MatcherAssert.assertThat(httpResponse.getStatus(), Matchers.is(201));
        MatcherAssert.assertThat(httpResponse.getHeaderString(HttpHeaders.LOCATION), Matchers.containsString(customerId.toString()));
        httpResponse.close();
    }

    public default Response createPhotoResponse(LoginResponse loginResponse, UUID customerId, Integer version) throws Exception {
        URI imageFile = Resources.getResource(SAMPLE_FILE).toURI();
        final File fileToUpload = new File(imageFile);
        String url = UploadCustomerPhotoResource.createResourcePath(customerId);
        Response httpResponse = authorizedRequest(loginResponse, url).header(HttpHeaders.IF_MATCH, version).post(Entity.entity(fileToUpload, TYPE));
        return httpResponse;
    }
}
