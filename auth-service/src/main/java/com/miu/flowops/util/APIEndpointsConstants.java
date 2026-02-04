package com.miu.flowops.util;

public class APIEndpointsConstants {

    private APIEndpointsConstants() {
    }

    public static final String[] PUBLIC_ENDPOINTS = {
            "/auth/**",           // Gateway strips /api/v1, so service receives /auth/**
            "/actuator/**",       // Health checks and metrics
            "/v3/api-docs/**",    // API documentation
            "/swagger-ui/**"      // Swagger UI
    };
}

