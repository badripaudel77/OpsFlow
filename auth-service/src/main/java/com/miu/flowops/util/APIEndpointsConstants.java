package com.miu.flowops.util;

public class APIEndpointsConstants {
    public static final String ADMIN = "ADMIN";

    private APIEndpointsConstants() {
    }

    public static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/**",
            "/api/public/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/actuator/**"
    };

    public static final String[] ADMIN_ONLY_ENDPOINTS = {
            "/api/releases/**"
    };
}

