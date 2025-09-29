package com.example.controller;

import com.example.dto.auth.AuthResponse;
import com.example.dto.auth.LoginRequest;
import com.example.dto.auth.RegisterRequest;
import com.example.http.HttpUtils;
import com.example.model.enums.UserRole;
import com.example.security.JwtService;
import com.example.service.UserService;
import com.example.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.example.service.CartService;

import java.io.IOException;
import java.util.Map;

public class AuthController {
    private final UserService users;
    private final JwtService jwt;

    public AuthController(UserService users, JwtService jwt) {
        this.users = users; this.jwt = jwt;
    }

    public void handleLogin(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "POST"); return; }
        var req = JsonUtil.fromJson(HttpUtils.readBody(ex), LoginRequest.class);
        if (req.username == null || req.password == null) { HttpUtils.sendJson(ex, 400, Map.of("error","bad_request")); return; }

        var uOpt = users.login(req.username, req.password);
        if (uOpt.isEmpty()) { HttpUtils.sendJson(ex, 401, Map.of("error","invalid_credentials")); return; }

        var u = uOpt.get();
        var role = u.getRole() != null ? u.getRole().name() : UserRole.CUSTOMER.name();
        String token = jwt.issue(u.getId(), u.getUsername(), role);

        Integer cartIdHint = HttpUtils.tryGetCookieInt(ex, "cartId");
        int cartId = new CartService().ensureCart(u.getId(), cartIdHint);
        HttpUtils.setCookie(ex, "cartId", String.valueOf(cartId), 60 * 60 * 24 * 30);

        HttpUtils.sendJson(ex, 200, new AuthResponse(u.getUsername(), token));
    }

    public void handleRegister(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "POST"); return; }
        var req = JsonUtil.fromJson(HttpUtils.readBody(ex), RegisterRequest.class);
        if (req.fullname == null || req.username == null || req.password == null) { HttpUtils.sendJson(ex, 400, Map.of("error","bad_request")); return; }

        boolean ok = users.register(req.fullname, req.username, req.password);
        if (!ok) { HttpUtils.sendJson(ex, 409, Map.of("error","username_exists")); return; }

        var uOpt = users.login(req.username, req.password);
        if (uOpt.isEmpty()) { HttpUtils.sendJson(ex, 500, Map.of("error","register_login_failed")); return; }

        var u = uOpt.get();
        String token = jwt.issue(u.getId(), u.getUsername(), UserRole.CUSTOMER.name());

        Integer cartIdHint = HttpUtils.tryGetCookieInt(ex, "cartId");
        int cartId = new CartService().ensureCart(u.getId(), cartIdHint);
        HttpUtils.setCookie(ex, "cartId", String.valueOf(cartId), 60 * 60 * 24 * 30);

        HttpUtils.sendJson(ex, 201, new AuthResponse(u.getUsername(), token));
    }
    public void handleLogout(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        HttpUtils.setCookie(ex, "cartId", "", 0);
        HttpUtils.sendStatus(ex, 204);
    }
}
