package com.example.client;

import com.example.dto.AuthDtos.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class ApiClient {
    private final String baseUrl;
    private final HttpClient http;
    private final ObjectMapper json = new ObjectMapper();
    private String authToken; // JWT

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length()-1) : baseUrl;
        this.http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    public void setAuthToken(String jwt) { this.authToken = jwt; }
    public String getAuthToken() { return authToken; }

    private HttpRequest.Builder req(String method, String path) {
        HttpRequest.Builder b = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(8));
        if ("GET".equals(method)) b = b.GET();
        if (authToken != null) b.header("Authorization", "Bearer " + authToken);
        b.header("Content-Type", "application/json");
        return b;
    }

    private <T> T handle(HttpResponse<String> resp, Class<T> clazz) throws ApiException {
        int s = resp.statusCode();
        String body = resp.body();
        if (s >= 200 && s < 300) {
            try { return clazz == Void.class ? null : json.readValue(body, clazz); }
            catch (IOException e) { throw new ApiException(s, "Failed to parse response JSON"); }
        }
        try {
            var node = json.readTree(body);
            String code = node.has("code") ? node.get("code").asText() : null;
            String message = node.has("message") ? node.get("message").asText() : body;
            throw new ApiException(s, code, message);
        } catch (IOException ignore) {
            throw new ApiException(s, body);
        }
    }

    private String toJson(Object o) throws ApiException {
        try { return json.writeValueAsString(o); } catch (Exception e) { throw new ApiException(0, "JSON error: "+e.getMessage()); }
    }

    public AuthResult login(String username, String password) throws ApiException {
        try {
            var payload = new LoginRequest(username, password);
            var req = req("POST", "/auth/login")
                    .POST(HttpRequest.BodyPublishers.ofString(toJson(payload), StandardCharsets.UTF_8))
                    .build();
            var resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            var result = handle(resp, AuthResult.class);
            if (result != null && result.token != null) setAuthToken(result.token);
            return result;
        } catch (IOException | InterruptedException e) {
            throw new ApiException(0, "Network error: " + e.getMessage());
        }
    }

    public AuthResult register(String fullName, String username, String password) throws ApiException {
        try {
            var payload = new RegisterRequest(fullName, username, password);
            var req = req("POST", "/auth/register")
                    .POST(HttpRequest.BodyPublishers.ofString(toJson(payload), StandardCharsets.UTF_8))
                    .build();
            var resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            var result = handle(resp, AuthResult.class);
            if (result != null && result.token != null) setAuthToken(result.token);
            return result;
        } catch (IOException | InterruptedException e) {
            throw new ApiException(0, "Network error: " + e.getMessage());
        }
    }
}