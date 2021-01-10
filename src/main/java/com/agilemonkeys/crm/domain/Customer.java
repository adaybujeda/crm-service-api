package com.agilemonkeys.crm.domain;

import com.google.common.base.MoreObjects;

import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import java.util.UUID;

public class Customer {

    private final UUID customerId;
    private final String providedId;
    private final String name;
    private final String surname;
    private final UUID photoId;
    private final Integer version;
    private final LocalDateTime createdDate;
    private final UUID createdBy;
    private final LocalDateTime updatedDate;
    private final UUID updatedBy;

    @ConstructorProperties({"customer_id", "provided_id", "name", "surname", "photo_id", "version", "created_date", "created_by", "updated_date", "updated_by"})
    public Customer(UUID customerId, String providedId, String name, String surname, UUID photoId, Integer version, LocalDateTime createdDate, UUID createdBy, LocalDateTime updatedDate, UUID updatedBy) {
        this.customerId = customerId;
        this.providedId = providedId;
        this.name = name;
        this.surname = surname;
        this.photoId = photoId;
        this.version = version;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.updatedDate = updatedDate;
        this.updatedBy = updatedBy;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getProvidedId() {
        return providedId;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public UUID getPhotoId() {
        return photoId;
    }

    public Integer getVersion() {
        return version;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public UUID getUpdatedBy() {
        return updatedBy;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("customerId", customerId)
                .add("providedId", providedId)
                .add("version", version)
                .add("updatedDate", updatedDate)
                .toString();
    }
}
