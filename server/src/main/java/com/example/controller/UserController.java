package com.example.controller;

import com.example.dao.OrderDao;
import com.example.model.enums.OrderStatus;
import com.example.dto.RoleUpdateRequest;
import com.example.dto.UserUpdateRequest;
import com.example.http.HttpUtils;
import com.example.model.User;
import com.example.model.enums.UserRole;
import com.example.security.JwtService;
import com.example.service.CartService;
import com.example.service.UserService;
import com.example.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class UserController {

    private final UserService users;
    private final OrderDao orderDao;
    private final JwtService jwt;
    private final CartService cartService;

    public UserController(UserService users, OrderDao orderDao, JwtService jwt, CartService cartService) {
        this.users = users;
        this.orderDao = orderDao;
        this.jwt = jwt;
        this.cartService = cartService;
    }

    // ===== helpers =====
    private UserRole role(HttpExchange ex) {
        Object r = ex.getAttribute("role");
        if (r instanceof String s) {
            try { return UserRole.valueOf(s.toUpperCase()); }
            catch (IllegalArgumentException ignored) {}
        }
        return UserRole.CUSTOMER;
    }

    private String username(HttpExchange ex) {
        Object u = ex.getAttribute("user");
        return (u instanceof String s) ? s : null;
    }

    // ===== /users/me =====

    /** GET /users/me */
    public void getMe(HttpExchange ex) throws IOException {
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "GET"); return; }
        String uname = username(ex);
        if (uname == null) { HttpUtils.sendJson(ex, 401, Map.of("error", "unauthorized")); return; }

        Optional<User> u = users.findByUsername(uname);
        if (u.isEmpty()) { HttpUtils.sendJson(ex, 404, Map.of("error", "not_found")); return; }

        User me = u.get();
        me.setPassword(null);
        HttpUtils.sendJson(ex, 200, me);
    }

    /** PUT /users/me — edit own profile */
    public void updateMe(HttpExchange ex) throws IOException {
        if (!"PUT".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "PUT"); return; }
        String uname = username(ex);
        if (uname == null) { HttpUtils.sendJson(ex, 401, Map.of("error", "unauthorized")); return; }

        UserUpdateRequest req = JsonUtil.fromJson(HttpUtils.readBody(ex), UserUpdateRequest.class);
        if (req == null) { HttpUtils.sendJson(ex, 400, Map.of("error", "bad_request")); return; }

        Optional<User> updated = users.updateProfile(uname, req.fullname, req.username, req.password);
        if (updated.isEmpty()) { HttpUtils.sendJson(ex, 400, Map.of("error", "cannot_update")); return; }

        User me = updated.get();
        me.setPassword(null);

        String newToken = null;
        if (!uname.equals(me.getUsername())) {
            newToken = jwt.issue(me.getId(), me.getUsername(), me.getRole().name());
        }

        if (newToken != null) {
            HttpUtils.sendJson(ex, 200, Map.of(
                    "user", me,
                    "token", newToken
            ));
        } else {
            HttpUtils.sendJson(ex, 200, Map.of(
                    "user", me
            ));
        }
    }

    /** DELETE /users/me — account owner delete profile */
    public void deleteMe(HttpExchange ex) throws IOException {
        if (!"DELETE".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "DELETE"); return; }
        String uname = username(ex);
        if (uname == null) { HttpUtils.sendJson(ex, 401, Map.of("error","unauthorized")); return; }

        boolean ok = users.deleteByUsername(uname);
        if (!ok) { HttpUtils.sendJson(ex, 404, Map.of("error","user_not_found")); return; }
        HttpUtils.sendJson(ex, 200, Map.of("status","deleted"));
    }

    // ===== Roles (ADMIN) =====

    /** PUT /users/{id}/role — onlu ADMIN */
    public void updateRoleById(HttpExchange ex) throws IOException {
        if (!"PUT".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "PUT"); return; }
        if (role(ex) != UserRole.ADMIN) { HttpUtils.sendJson(ex, 403, Map.of("error","forbidden")); return; }

        String[] parts = ex.getRequestURI().getPath().split("/");
        // /users/{id}/role -> ["", "users", "{id}", "role"]
        if (parts.length < 4) { HttpUtils.sendJson(ex, 400, Map.of("error","bad_path")); return; }

        int userId;
        try { userId = Integer.parseInt(parts[2]); }
        catch (NumberFormatException e) { HttpUtils.sendJson(ex, 400, Map.of("error","bad_id")); return; }

        RoleUpdateRequest req = JsonUtil.fromJson(HttpUtils.readBody(ex), RoleUpdateRequest.class);
        if (req == null || req.role == null) { HttpUtils.sendJson(ex, 400, Map.of("error","bad_request")); return; }

        UserRole newRole;
        try { newRole = UserRole.valueOf(req.role.toUpperCase()); }
        catch (IllegalArgumentException e) { HttpUtils.sendJson(ex, 400, Map.of("error","invalid_role")); return; }

        Optional<User> res = users.setRoleById(userId, newRole);
        if (res.isEmpty()) { HttpUtils.sendJson(ex, 404, Map.of("error","user_not_found")); return; }

        User u = res.get(); u.setPassword(null);
        HttpUtils.sendJson(ex, 200, u);
    }
    public void listMyOrders(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "GET");
        Integer userId = HttpUtils.tryGetUserId(ex, jwt);
        if (userId == null || userId <= 0) { HttpUtils.sendJson(ex, 401, Map.of("error","unauthorized")); return; }

        var q = HttpUtils.parseQuery(ex);
        String statusFilter = q.getOrDefault("status", "all");
        String sort = q.getOrDefault("sort", "ordered_desc");

        var all = orderDao.findByUserId(userId);
        var filtered = new java.util.ArrayList<com.example.model.Order>();
        for (var o : all) {
            if (o.getStatus() == OrderStatus.CART) continue;
            boolean keep = switch (statusFilter.toLowerCase()) {
                case "active"    -> o.getStatus() == OrderStatus.ORDERED
                        || o.getStatus() == OrderStatus.PREPARING
                        || o.getStatus() == OrderStatus.OUT_FOR_DELIVERY;
                case "delivered" -> o.getStatus() == OrderStatus.DELIVERED;
                case "cancelled" -> o.getStatus() == OrderStatus.CANCELLED;
                default          -> true;
            };
            if (keep) filtered.add(o);
        }

        filtered.sort((a,b) -> {
            var at = a.getOrderedAt(); var bt = b.getOrderedAt();
            int cmp = java.util.Objects.compare(at, bt, java.util.Comparator.nullsLast(java.util.Comparator.naturalOrder()));
            return "ordered_asc".equalsIgnoreCase(sort) ? cmp : -cmp;
        });

        var views = new java.util.ArrayList<com.example.dto.OrderHistoryView>();
        for (var o : filtered) {
            var cv = cartService.getCart(o.getId());
            views.add(new com.example.dto.OrderHistoryView(
                    cv.orderId(),
                    cv.status(),
                    cv.items(),
                    cv.total(),
                    o.getOrderedAt(),
                    o.getPreparingAt(),
                    o.getOutForDeliveryAt(),
                    o.getDeliveredAt(),
                    o.getCancelledAt(),
                    o.getDeliveryPhone(),
                    o.getDeliveryAddress()
            ));
        }

        HttpUtils.sendJson(ex, 200, views);
    }

    /** PUT /users/by-username/{username}/role — only ADMIN */
    public void updateRoleByUsername(HttpExchange ex) throws IOException {
        if (!"PUT".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "PUT"); return; }
        if (role(ex) != UserRole.ADMIN) { HttpUtils.sendJson(ex, 403, Map.of("error","forbidden")); return; }

        // /users/by-username/{username}/role
        String path = ex.getRequestURI().getPath();
        String[] parts = path.split("/", 5);
        if (parts.length < 5) { HttpUtils.sendJson(ex, 400, Map.of("error","bad_path")); return; }
        String uname = parts[3];

        RoleUpdateRequest req = JsonUtil.fromJson(HttpUtils.readBody(ex), RoleUpdateRequest.class);
        if (req == null || req.role == null) { HttpUtils.sendJson(ex, 400, Map.of("error","bad_request")); return; }

        UserRole newRole;
        try { newRole = UserRole.valueOf(req.role.toUpperCase()); }
        catch (IllegalArgumentException e) { HttpUtils.sendJson(ex, 400, Map.of("error","invalid_role")); return; }

        Optional<User> res = users.setRoleByUsername(uname, newRole);
        if (res.isEmpty()) { HttpUtils.sendJson(ex, 404, Map.of("error","user_not_found")); return; }

        User u = res.get(); u.setPassword(null);
        HttpUtils.sendJson(ex, 200, u);
    }

    // ===== Deleting (ADMIN) =====

    /** DELETE /users/{id} — only ADMIN */
    public void deleteById(HttpExchange ex) throws IOException {
        if (!"DELETE".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "DELETE"); return; }
        if (role(ex) != UserRole.ADMIN) { HttpUtils.sendJson(ex, 403, Map.of("error","forbidden")); return; }

        String[] parts = ex.getRequestURI().getPath().split("/");
        if (parts.length < 3) { HttpUtils.sendJson(ex, 400, Map.of("error","bad_path")); return; }

        int userId;
        try { userId = Integer.parseInt(parts[2]); }
        catch (NumberFormatException e) { HttpUtils.sendJson(ex, 400, Map.of("error","bad_id")); return; }

        String current = username(ex);
        Optional<User> target = users.findById(userId);
        if (target.isPresent() && current != null && current.equals(target.get().getUsername())) {
            HttpUtils.sendJson(ex, 400, Map.of("error","cannot_delete_self")); return;
        }

        boolean ok = users.deleteById(userId);
        if (!ok) { HttpUtils.sendJson(ex, 404, Map.of("error","user_not_found")); return; }
        HttpUtils.sendJson(ex, 200, Map.of("status","deleted"));
    }

    /** DELETE /users/by-username/{username} — only ADMIN */
    public void deleteByUsername(HttpExchange ex) throws IOException {
        if (!"DELETE".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "DELETE"); return; }
        if (role(ex) != UserRole.ADMIN) { HttpUtils.sendJson(ex, 403, Map.of("error","forbidden")); return; }

        String[] parts = ex.getRequestURI().getPath().split("/", 4);
        if (parts.length < 4) { HttpUtils.sendJson(ex, 400, Map.of("error","bad_path")); return; }
        String uname = parts[3];

        String current = username(ex);
        if (current != null && current.equals(uname)) {
            HttpUtils.sendJson(ex, 400, Map.of("error","cannot_delete_self")); return;
        }

        boolean ok = users.deleteByUsername(uname);
        if (!ok) { HttpUtils.sendJson(ex, 404, Map.of("error","user_not_found")); return; }
        HttpUtils.sendJson(ex, 200, Map.of("status","deleted"));
    }
}
