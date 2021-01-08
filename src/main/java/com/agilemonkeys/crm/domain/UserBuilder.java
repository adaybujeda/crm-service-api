package com.agilemonkeys.crm.domain;

import com.agilemonkeys.crm.resources.user.CreateUserRequest;
import com.agilemonkeys.crm.resources.user.UpdateUserRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserBuilder {
    private UUID userId;
    private String name;
    private String username;
    private String passwordHash;
    private UserRole role = UserRole.USER;
    private Integer version = 1;
    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime updatedDate = LocalDateTime.now();

    private UserBuilder(){
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }
    public static UserBuilder fromUser(User user) {
        return new UserBuilder().withUserId(user.getUserId())
                .withName(user.getName())
                .withNormalizedUsername(user.getUsername())
                .withPasswordHash(user.getPasswordHash())
                .withRole(user.getRole())
                .withVersion(user.getVersion())
                .withCreatedDate(user.getCreatedDate())
                .withUpdatedDate(user.getUpdatedDate());
    }

    public static UserBuilder fromRequest(CreateUserRequest request) {
        return new UserBuilder().withName(request.getName())
                .withNormalizedUsername(request.getUsername())
                .withRole(request.getRole());
    }

    public static UserBuilder fromRequest(UpdateUserRequest request) {
        return new UserBuilder().withName(request.getName())
                .withNormalizedUsername(request.getUsername())
                .withRole(request.getRole());
    }

    public UserBuilder withUserId(UUID userId) {
        this.userId = userId;
        return this;
    }

    public UserBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder withNormalizedUsername(String username) {
        this.username = username == null ? null : username.trim().toLowerCase();
        return this;
    }

    public UserBuilder withPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public UserBuilder withRole(UserRole role) {
        this.role = role;
        return this;
    }

    public UserBuilder withVersion(Integer version) {
        this.version = version;
        return this;
    }

    public UserBuilder withNextVersion() {
        this.version = version + 1;
        return this;
    }

    public UserBuilder withCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public UserBuilder withUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
        return this;
    }

    public UserBuilder withUpdatedDate() {
        this.updatedDate = LocalDateTime.now();
        return this;
    }

    public User build() {
        return new User(userId, name, username, passwordHash, role, version, createdDate, updatedDate);
    }
}
