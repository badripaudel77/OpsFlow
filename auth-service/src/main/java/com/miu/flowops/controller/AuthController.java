package com.miu.flowops.controller;

import com.miu.flowops.dto.*;
import com.miu.flowops.model.Role;
import com.miu.flowops.model.User;
import com.miu.flowops.repository.UserRepository;
import com.miu.flowops.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @GetMapping()
    public String welcome() {
        return "Welcome to OpsFlow, your ultimate workflow manager. Please Login to access the features.";
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username()))
            return "Username already exists";

        if (userRepository.existsByEmail(request.email()))
            return "Email already exists";

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(request.roles().stream()
                        .map(String::toUpperCase)
                        .map(Role::valueOf)
                        .collect(Collectors.toSet()))
                .build();

        userRepository.save(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.password(), user.getPassword()))
            throw new RuntimeException("Invalid password");

        String token = jwtUtil.generateAccessToken(user.getId(), user.getUsername(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()));

        String primaryRole = user.getRoles().stream().findFirst().get().name();

        return new AuthResponse(token, user.getUsername(), primaryRole);
    }
}
