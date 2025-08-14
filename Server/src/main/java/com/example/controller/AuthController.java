// src/com/example/controller/AuthController.java
package com.example.controller;

import com.example.dto.AuthResponse;
import com.example.dto.LoginRequest;
import com.example.dto.RegisterRequest;
import com.example.http.HttpUtils;
import com.example.security.TokenService;
import com.example.service.UserService;
import com.example.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Optional;

public class AuthController {
    private final UserService users;
    private final TokenService tokens;

    public AuthController(UserService users, TokenService tokens){ this.users=users; this.tokens=tokens; }

    public void handleLogin(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "POST"); return; }
        var req = JsonUtil.fromJson(HttpUtils.readBody(ex), LoginRequest.class);
        if (req.username == null || req.password == null) { HttpUtils.sendJson(ex, 400, java.util.Map.of("error","bad_request")); return; }
        Optional<com.example.model.User> u = users.login(req.username, req.password);
        if (u.isEmpty()) { HttpUtils.sendJson(ex, 401, java.util.Map.of("error","invalid_credentials")); return; }
        String token = tokens.issueToken(req.username);
        HttpUtils.sendJson(ex, 200, new AuthResponse(req.username, token));
    }

    public void handleRegister(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "POST"); return; }
        var req = JsonUtil.fromJson(HttpUtils.readBody(ex), RegisterRequest.class);
        if (req.fullname == null || req.username == null || req.password == null) { HttpUtils.sendJson(ex, 400, java.util.Map.of("error","bad_request")); return; }
        boolean ok = users.register(req.fullname, req.username, req.password);
        if (!ok) { HttpUtils.sendJson(ex, 409, java.util.Map.of("error","username_exists")); return; }
        String token = tokens.issueToken(req.username);
        HttpUtils.sendJson(ex, 201, new AuthResponse(req.username, token));
    }
}
