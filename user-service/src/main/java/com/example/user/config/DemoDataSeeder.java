package com.example.user.config;

import com.example.user.model.AppUser;
import com.example.user.model.UserRole;
import com.example.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
public class DemoDataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public DemoDataSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        seed("user@example.com", "Demo User", "password123", Set.of(UserRole.USER));
        seed("admin@example.com", "Demo Admin", "admin12345", Set.of(UserRole.USER, UserRole.ADMIN));
    }

    private void seed(String email, String fullName, String password, Set<UserRole> roles) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRoles(new LinkedHashSet<>(roles));
        userRepository.save(user);
    }
}
