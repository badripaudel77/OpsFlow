package com.miu.flowops.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public Mono<ResponseEntity<Map<String, Object>>> fallback() {
        return Mono.just(ResponseEntity.ok(Map.of(
                "timestamp", Instant.now().toString(),
                "status", 503,
                "message", "Service is temporarily unavailable. Please try again later."
        )));
    }

    @GetMapping("/")
    public Mono<ResponseEntity<Map<String, Object>>> home() {
        return Mono.just(ResponseEntity.ok(Map.of(
                "service", "OpsFlow API Gateway",
                "version", "1.0.0",
                "timestamp", Instant.now().toString(),
                "status", "running"
        )));
    }
}
