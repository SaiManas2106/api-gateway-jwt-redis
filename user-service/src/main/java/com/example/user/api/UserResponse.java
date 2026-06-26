package com.example.user.api;

import com.example.user.model.AppUser;
import com.example.user.model.UserRole;

import java.util.Set;

public record UserResponse(Long id, String email, String fullName, Set<UserRole> roles) {
    public static UserResponse from(AppUser user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRoles());
    }
}
