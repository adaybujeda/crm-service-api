package com.agilemonkeys.crm.domain;

import com.agilemonkeys.crm.resources.CreateUpdateUserRequest;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserBuilder {
    private UUID userId;
    private String name;
    private String username;
    private String password;
    private UserRole role = UserRole.USER;
    private Integer version = 1;
    private LocalDateTime createdDate = LocalDateTime.now();
    private LocalDateTime updatedDate = LocalDateTime.now();

    public static UserBuilder fromUser(User user) {
        return new UserBuilder().withUserId(user.getUserId())
                .withName(user.getName())
                .withUsername(user.getUsername())
                .withPassword(user.getPassword())
                .withRole(user.getRole())
                .withVersion(user.getVersion())
                .withCreatedDate(user.getCreatedDate())
                .withUpdatedDate(user.getUpdatedDate());
    }

    public static UserBuilder fromRequest(CreateUpdateUserRequest request) {
        return new UserBuilder().withName(request.getName())
                .withUsername(request.getUsername())
                .withPassword(request.getPassword())
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

    public UserBuilder withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder withPassword(String password) {
        this.password = password;
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

    public UserBuilder withCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public UserBuilder withUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
        return this;
    }

    public User build() {
        return new User(userId, name, username, password, role, version, createdDate, updatedDate);
    }
}
