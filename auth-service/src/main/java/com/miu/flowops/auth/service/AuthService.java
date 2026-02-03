package com.miu.flowops.auth.service;

import com.miu.flowops.auth.dto.*;
import com.miu.flowops.auth.exception.AuthException;
import com.miu.flowops.auth.model.User;
import com.miu.flowops.auth.repository.UserRepository;
import com.miu.flowops.auth.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public void register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException("Email already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(request.getRole())
                .createdAt(Instant.now())
                .isActive(true)
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new AuthException("Account is deactivated");
        }

        String token = jwtUtil.generateToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(jwtUtil.getExpiration() / 1000) // Convert to seconds
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .role(user.getRole())
                        .build())
                .build();
    }

    public TokenValidationResponse validateToken(String token) {
        try {
            Claims claims = jwtUtil.validateToken(token);
            String tokenType = claims.get("type", String.class);
            
            if (!"access".equals(tokenType)) {
                return TokenValidationResponse.builder()
                        .valid(false)
                        .message("Invalid token type")
                        .build();
            }

            return TokenValidationResponse.builder()
                    .valid(true)
                    .userId(claims.getSubject())
                    .email(claims.get("email", String.class))
                    .role(com.miu.flowops.auth.model.Role.valueOf(claims.get("role", String.class)))
                    .build();
        } catch (ExpiredJwtException e) {
            return TokenValidationResponse.builder()
                    .valid(false)
                    .message("Token has expired")
                    .build();
        } catch (JwtException e) {
            return TokenValidationResponse.builder()
                    .valid(false)
                    .message("Invalid token")
                    .build();
        }
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        try {
            Claims claims = jwtUtil.validateToken(request.getRefreshToken());
            String tokenType = claims.get("type", String.class);
            
            if (!"refresh".equals(tokenType)) {
                throw new AuthException("Invalid refresh token");
            }

            String userId = claims.getSubject();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AuthException("User not found"));

            if (!user.isActive()) {
                throw new AuthException("Account is deactivated");
            }

            String newToken = jwtUtil.generateToken(user);
            String newRefreshToken = jwtUtil.generateRefreshToken(user);

            return AuthResponse.builder()
                    .token(newToken)
                    .refreshToken(newRefreshToken)
                    .expiresIn(jwtUtil.getExpiration() / 1000)
                    .user(AuthResponse.UserInfo.builder()
                            .id(user.getId())
                            .email(user.getEmail())
                            .fullName(user.getFullName())
                            .role(user.getRole())
                            .build())
                    .build();
        } catch (JwtException e) {
            throw new AuthException("Invalid or expired refresh token");
        }
    }

    public AuthResponse.UserInfo getCurrentUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException("User not found"));

        return AuthResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }
}
