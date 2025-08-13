package com.example.controller;

import com.example.model.User;
import com.example.network.SessionManager;
import com.example.service.UserService;
import com.example.utils.JsonResponse;
import com.example.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Optional;
import java.util.UUID;

public class AuthController {

    private final UserService userService = new UserService();

    public JsonNode handle(String action, JsonNode payload) {
        return switch (action) {
            case "auth:login" -> login(payload);
            case "auth:register" -> register(payload);
            default -> JsonResponse.error("Unknown auth action");
        };
    }

    private JsonNode login(JsonNode payload) {
        if (!payload.has("username") || !payload.has("password")) {
            return JsonResponse.error("Missing username or password");
        }

        String username = payload.get("username").asText();
        String password = payload.get("password").asText();

        Optional<User> optionalUser = userService.login(username, password);

        if (optionalUser.isEmpty()) {
            return JsonResponse.error("Invalid credentials");
        }

        User user = optionalUser.get();
        user.setPassword(null);

        String token = UUID.randomUUID().toString();
        SessionManager.store(token, user);

        return JsonUtil.mapper.createObjectNode()
                .put("status", "success")
                .put("message", "Login successful")
                .put("authToken", token)
                .set("data", JsonUtil.mapper.valueToTree(user));
    }

    private JsonNode register(JsonNode payload) {
        if (!payload.has("fullname") || !payload.has("username") || !payload.has("password")) {
            return JsonResponse.error("Missing fields");
        }

        String fullname = payload.get("fullname").asText();
        String username = payload.get("username").asText();
        String password = payload.get("password").asText();

        if (userService.existsByUsername(username)) {
            return JsonResponse.error("Username already exists");
        }

        boolean success = userService.register(fullname, username, password);
        return success
                ? JsonResponse.success("Registration successful")
                : JsonResponse.error("Registration failed");
    }
}
