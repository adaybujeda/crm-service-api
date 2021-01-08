package com.agilemonkeys.crm.domain;

import com.google.common.base.MoreObjects;

import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import java.util.UUID;

public class CustomerPhoto {

    private final UUID customerId;
    private final String contentType;
    private final byte[] photo;
    private final LocalDateTime createdDate;

    @ConstructorProperties({"customer_id", "content_type", "photo", "created_date",})
    public CustomerPhoto(UUID customerId, String contentType, byte[] photo, LocalDateTime createdDate) {
        this.customerId = customerId;
        this.contentType = contentType;
        this.photo = photo;
        this.createdDate = createdDate;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("customerId", customerId)
                .add("contentType", contentType)
                .add("size", photo.length)
                .add("createdDate", createdDate)
                .toString();
    }
}
