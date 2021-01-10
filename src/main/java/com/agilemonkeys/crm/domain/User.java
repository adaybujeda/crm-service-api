package com.agilemonkeys.crm.domain;

import com.google.common.base.MoreObjects;

import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private final UUID userId;
    private final String name;
    private final String username;
    private final String passwordHash;
    private final UserRole role;
    private final Integer version;
    private final LocalDateTime createdDate;
    private final LocalDateTime updatedDate;
    private final LocalDateTime deletedDate;

    @ConstructorProperties({"user_id", "name", "username", "password_hash", "role", "version", "created_date", "updated_date", "deleted_date"})
    public User(UUID userId, String name, String username, String passwordHash, UserRole role, Integer version, LocalDateTime createdDate, LocalDateTime updatedDate, LocalDateTime deletedDate) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.version = version;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.deletedDate = deletedDate;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public Integer getVersion() {
        return version;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public LocalDateTime getDeletedDate() {
        return deletedDate;
    }

    public boolean isDeleted() {
        return deletedDate != null;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userId", userId)
                .add("role", role)
                .add("version", version)
                .add("updatedDate", updatedDate)
                .add("deletedDate", deletedDate)
                .toString();
    }
}
