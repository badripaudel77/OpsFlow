package com.miu.flowops.util;

import java.util.List;

public class Constants {
    private Constants() {
        // Prevent instantiation
    }

    public static final List<String> SKIP_FILTER_ENDPOINTS = List.of(
            "/api/v1/auth"
    );
}
