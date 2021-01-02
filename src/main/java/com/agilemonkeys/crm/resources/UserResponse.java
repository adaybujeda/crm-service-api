package com.agilemonkeys.crm.resources;

import com.agilemonkeys.crm.domain.User;
import com.agilemonkeys.crm.domain.UserRole;

import java.util.List;
import java.util.UUID;

public class UserResponse {

    private UUID userId;
    private String name;
    private String username;
    private UserRole role;

    public UserResponse(UUID userId, String name, String username, UserRole role) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.role = role;
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

    public static UserResponse fromUser(User user) {
        return new UserResponse(user.getUserId(), user.getName(), user.getUsername(), user.getRole());
    }

    public static class UserResponseCollection {
        private List<UserResponse> items;

        public UserResponseCollection(List<UserResponse> items) {
            this.items = items;
        }

        public List<UserResponse> getItems() {
            return items;
        }
    }
}
