package com.miu.flowops.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class RequestLoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Generate correlation ID for request tracing
        String correlationId = UUID.randomUUID().toString().substring(0, 8);
        
        long startTime = System.currentTimeMillis();
        
        log.info("[{}] Incoming request: {} {} from {}", 
                correlationId,
                request.getMethod(), 
                request.getPath(),
                request.getRemoteAddress());

        // Add correlation ID to request headers
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(r -> r.header("X-Correlation-Id", correlationId))
                .build();

        return chain.filter(mutatedExchange).then(Mono.fromRunnable(() -> {
            long duration = System.currentTimeMillis() - startTime;
            log.info("[{}] Response: {} {} - Status: {} - Duration: {}ms",
                    correlationId,
                    request.getMethod(),
                    request.getPath(),
                    exchange.getResponse().getStatusCode(),
                    duration);
        }));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
