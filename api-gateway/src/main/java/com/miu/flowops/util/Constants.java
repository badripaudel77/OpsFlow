package com.miu.flowops.util;

import java.util.List;

public class Constants {
    public static final String SECRET_TOKEN = "flowopsSecretKeykeepthisverylongandsecretforsecuritypurposeotherwisepeoplewillstealit";
    public static final List<String> SKIP_FILTER_ENDPOINTS = List.of(
            "/api/v1/auth"
    );
}
