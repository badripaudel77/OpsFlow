package com.miu.flowops.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Set;

@Component
public class JwtUtil {
    @Getter
    private String secret = "flowopsSecretKeykeepthisverylongandsecretforsecuritypurposeotherwisepeoplewillstealit";
    private long accessTokenValidityMs = 3600_000;

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String username, Set<String> roles) {
        String rolesString = String.join(",", roles);

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", rolesString)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidityMs))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

}
