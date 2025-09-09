package com.example.security;

import com.example.model.enums.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class JwtService {
    private final SecretKey key;
    private final long ttlSeconds;

    public JwtService(String base64Secret, long ttlSeconds) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.ttlSeconds = ttlSeconds;
    }

    public String issue(String username, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .claims(Map.of("role", role))
                .signWith(key)
                .compact();
    }

    public JwtUser verify(String token) {
        var jwt = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
        var body = jwt.getPayload();
        String sub = body.getSubject();
        String role = body.get("role", String.class);
        Date exp = body.getExpiration();
        return new JwtUser(sub, role, exp);
    }

    public static class JwtUser {
        public final String username;
        public final String role;
        public final Date expiresAt;
        public JwtUser(String u, String r, Date e){ this.username=u; this.role=r; this.expiresAt=e; }
    }
    public UserRole extractUserRole(String token) {
        var jwt = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

        String role = jwt.getPayload().get("role", String.class);
        if (role == null) return UserRole.CUSTOMER;

        try {
            return UserRole.valueOf(role.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return UserRole.CUSTOMER;
        }
    }
    public int verifyAndGetUserId(String token){
        var jwt = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

        int userId = jwt.getPayload().get("id", Integer.class);
        return userId;
    }


}
