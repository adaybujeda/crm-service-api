package com.agilemonkeys.crm.domain;

import com.google.common.base.MoreObjects;

import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class User {

    private UUID userId;
    private String name;
    private String username;
    private String password;
    private UserRole role;
    private Integer version;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @ConstructorProperties({"user_id", "name", "username", "password", "role", "version", "created_date", "updated_date"})
    public User(UUID userId, String name, String username, String password, UserRole role, Integer version, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
        this.version = version;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
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

    public String getPassword() {
        return password;
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        final User other = (User) obj;
        return Objects.equals(this.userId, other.userId)
                && Objects.equals(this.name, other.name)
                && Objects.equals(this.username, other.username)
                && Objects.equals(this.password, other.password)
                && Objects.equals(this.role, other.role)
                && Objects.equals(this.version, other.version)
                && Objects.equals(this.createdDate, other.createdDate)
                && Objects.equals(this.updatedDate, other.updatedDate);

    }

    @Override
    public int hashCode() {
        return Objects.hash(
                this.userId, this.name, this.username, this.password, this.role, this.version, this.createdDate, this.updatedDate);

    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("userId", userId)
                .add("role", role)
                .add("version", version)
                .add("updatedDate", updatedDate)
                .toString();
    }
}
