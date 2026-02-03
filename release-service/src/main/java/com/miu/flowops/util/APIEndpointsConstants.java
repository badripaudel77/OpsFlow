package com.miu.flowops.util;

public class APIEndpointsConstants {
    public static final String ADMIN = "ADMIN";

    private APIEndpointsConstants() {
    }

    public static final String[] PUBLIC_ENDPOINTS = {
            "/actuator/**",       // Health checks and metrics
            "/v3/api-docs/**",    // API documentation
            "/swagger-ui/**"      // Swagger UI
    };

    public static final String[] ADMIN_ONLY_ENDPOINTS = {
            "/releases/**"        // Gateway strips /api/v1, so service receives /releases/**
    };
}

