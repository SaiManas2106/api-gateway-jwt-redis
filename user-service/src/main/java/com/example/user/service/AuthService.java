package com.example.user.service;

import com.example.user.api.AuthResponse;
import com.example.user.api.LoginRequest;
import com.example.user.api.RegisterRequest;
import com.example.user.api.UserResponse;
import com.example.user.exception.AuthException;
import com.example.user.model.AppUser;
import com.example.user.model.UserRole;
import com.example.user.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new IllegalArgumentException("Email already exists");
        }
        AppUser user = new AppUser();
        user.setEmail(request.email().toLowerCase());
        user.setFullName(request.fullName());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRoles(new LinkedHashSet<>(Set.of(UserRole.USER)));
        return response(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        AppUser user = userRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new AuthException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthException("Invalid credentials");
        }
        return response(user);
    }

    @Transactional(readOnly = true)
    public UserResponse me(String userId) {
        Long id = Long.parseLong(userId);
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new AuthException("User no longer exists"));
    }

    private AuthResponse response(AppUser user) {
        return new AuthResponse(jwtService.issue(user), "Bearer", jwtService.ttlSeconds(), UserResponse.from(user));
    }
}
