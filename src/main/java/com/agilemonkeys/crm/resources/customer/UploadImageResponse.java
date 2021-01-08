package com.agilemonkeys.crm.resources.customer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class UploadImageResponse {
    private final UUID imageId;

    @JsonCreator
    public UploadImageResponse(@JsonProperty("imageId") UUID imageId) {
        this.imageId = imageId;
    }

    public UUID getImageId() {
        return imageId;
    }
}
