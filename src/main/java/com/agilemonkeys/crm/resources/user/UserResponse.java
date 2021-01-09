package com.agilemonkeys.crm.resources.user;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserRole;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class UserResponse {

    private final UUID userId;
    private final String name;
    private final String username;
    private final UserRole role;
    private final LocalDateTime deletedDate;

    @JsonCreator
    public UserResponse(@JsonProperty("userId") UUID userId, @JsonProperty("name") String name, @JsonProperty("username") String username, @JsonProperty("role") UserRole role, @JsonProperty("deletedDate") LocalDateTime deletedDate) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.role = role;
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

    public UserRole getRole() {
        return role;
    }

    public LocalDateTime getDeletedDate() {
        return deletedDate;
    }

    public static UserResponse fromUser(User user) {
        return new UserResponse(user.getUserId(), user.getName(), user.getUsername(), user.getRole(), user.getDeletedDate());
    }

    public static class UserResponseCollection {
        private List<UserResponse> items;

        @JsonCreator
        public UserResponseCollection(@JsonProperty("items") List<UserResponse> items) {
            this.items = items;
        }

        public List<UserResponse> getItems() {
            return items;
        }
    }
}
