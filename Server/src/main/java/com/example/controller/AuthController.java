package com.example.controller;

import com.example.model.User;
import com.example.network.SessionManager;
import com.example.service.UserService;
import com.example.utils.JsonResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.Optional;
import java.util.UUID;

public class AuthController {

    private final UserService userService = new UserService();
    private final Gson gson = new Gson();

    public JsonObject handle(String action, JsonObject payload) {
        return switch (action) {
            case "auth:login" -> login(payload);
            case "auth:register" -> register(payload);
            default -> JsonResponse.error("Unknown auth action");
        };
    }

    private JsonObject login(JsonObject payload) {
        if (!payload.has("username") || !payload.has("password")) {
            return JsonResponse.error("Missing username or password");
        }

        String username = payload.get("username").getAsString();
        String password = payload.get("password").getAsString();

        Optional<User> optionalUser = userService.login(username, password);

        if (optionalUser.isEmpty()) {
            return JsonResponse.error("Invalid credentials");
        }

        User user = optionalUser.get();
        user.setPassword(null);

        String token = UUID.randomUUID().toString();
        SessionManager.store(token, user);

        JsonObject response = new JsonObject();
        response.addProperty("status", "success");
        response.addProperty("message", "Login successful");
        response.addProperty("authToken", token);
        response.add("data", gson.toJsonTree(user));

        return response;
    }

    private JsonObject register(JsonObject payload) {
        if (!payload.has("fullname") || !payload.has("username") || !payload.has("password")) {
            return JsonResponse.error("Missing fields");
        }

        String fullname = payload.get("fullname").getAsString();
        String username = payload.get("username").getAsString();
        String password = payload.get("password").getAsString();

        if (userService.existsByUsername(username)) {
            return JsonResponse.error("Username already exists");
        }

        boolean success = userService.register(fullname, username, password);

        if (!success) {
            return JsonResponse.error("Registration failed");
        }

        return JsonResponse.success("Registration successful");
    }
}
