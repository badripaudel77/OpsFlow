package com.miu.flowops.security;

import com.miu.flowops.util.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${opsflow.JWT_SECRET}")
    public String SECRET_TOKEN;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // skip the public path like /auth
        return Constants.SKIP_FILTER_ENDPOINTS
                .stream()
                .anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // If no token is provided
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }
        String token = authHeader.substring(7); // Remove "Bearer "
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(SECRET_TOKEN.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // Extract username and roles
            String username = claims.getSubject();
            String userId = claims.get("userId", String.class);
            String roles = claims.get("roles", String.class);

            log.info("JWT validated for user: {} (userId: {})", username, userId);

            // Wrap request to add headers that will be forwarded to downstream services
            HttpServletRequestWrapper wrappedRequest = new HttpServletRequestWrapper(request) {
                @Override
                public String getHeader(String name) {
                    if ("X-User-Id".equals(name)) {
                        return userId;
                    } else if ("X-Username".equals(name)) {
                        return username;
                    } else if ("X-User-Roles".equals(name)) {
                        return roles;
                    }
                    return super.getHeader(name);
                }

                @Override
                public Enumeration<String> getHeaderNames() {
                    List<String> names = Collections.list(super.getHeaderNames());
                    names.add("X-User-Id");
                    names.add("X-Username");
                    names.add("X-User-Roles");
                    return Collections.enumeration(names);
                }

                @Override
                public Enumeration<String> getHeaders(String name) {
                    if ("X-User-Id".equals(name)) {
                        return Collections.enumeration(Collections.singletonList(userId));
                    } else if ("X-Username".equals(name)) {
                        return Collections.enumeration(Collections.singletonList(username));
                    } else if ("X-User-Roles".equals(name)) {
                        return Collections.enumeration(Collections.singletonList(roles));
                    }
                    return super.getHeaders(name);
                }
            };

            // Continue filter chain with wrapped request
            filterChain.doFilter(wrappedRequest, response);
        }
        catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired token");
            log.error("JWT validation failed: {}", e.getMessage());
        }
    }
}