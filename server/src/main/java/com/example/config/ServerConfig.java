package com.example.config;

public record ServerConfig(int port, String jwtBase64Secret, long jwtTtlSeconds, int threads) {
    public static ServerConfig fromEnv() {
        int port = Integer.getInteger("PORT", 8080);
        String secret = System.getenv().getOrDefault(
                "JWT_SECRET", "rZg9l5mVxqkz6j+QG3WkX1XzF9yR8m2cQ3ZrT5wY2pA=");
        long ttl = Long.getLong("JWT_TTL", 3600L);
        int threads = Math.max(4, Runtime.getRuntime().availableProcessors() * 2);
        return new ServerConfig(port, secret, ttl, threads);
    }
}