package com.example.controller;

import com.example.http.HttpUtils;
import com.example.model.User;
import com.example.model.enums.UserRole;
import com.example.security.JwtService;
import com.example.service.UserService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class AdminUserController {
    private final UserService users;
    private final JwtService jwt;

    public AdminUserController(UserService users, JwtService jwt) {
        this.users = users;
        this.jwt = jwt;
    }

    public void handleList(HttpExchange ex) throws IOException {
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            HttpUtils.methodNotAllowed(ex, "GET");
            return;
        }
        UserRole role = HttpUtils.roleOr(ex, UserRole.CUSTOMER, jwt);
        if (role != UserRole.ADMIN) {
            HttpUtils.sendJson(ex, 403, Map.of("error", "forbidden"));
            return;
        }

        Integer page = safeInt(HttpUtils.queryParam(ex, "page"));
        Integer size = safeInt(HttpUtils.queryParam(ex, "size"));
        String q = HttpUtils.queryParam(ex, "q");
        int limit = (size == null || size <= 0 || size > 200) ? 50 : size;
        int pageNum = (page == null || page < 1) ? 1 : page;

        List<User> all = users.getAllUsers();
        if (q != null && !q.isBlank()) {
            String term = q.toLowerCase();
            all = all.stream().filter(u ->
                    (u.getUsername() != null && u.getUsername().toLowerCase().contains(term)) ||
                            (u.getFullname() != null && u.getFullname().toLowerCase().contains(term)) ||
                            String.valueOf(u.getId()).contains(term)
            ).collect(Collectors.toList());
        }

        all.sort(Comparator.comparingInt(User::getId));

        int total = all.size();
        int from = Math.max(0, (pageNum - 1) * limit);
        int to = Math.min(total, from + limit);
        List<Map<String, Object>> items = (from >= to) ? List.of()
                : all.subList(from, to).stream().map(this::toView).toList();

        HttpUtils.sendJson(ex, 200, Map.of(
                "items", items,
                "total", total,
                "page", pageNum,
                "size", limit
        ));
    }

    private Map<String, Object> toView(User u) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", u.getId());
        m.put("username", u.getUsername());
        m.put("fullname", u.getFullname());
        m.put("role", u.getRole() != null ? u.getRole().name() : null);
        m.put("createdAt", u.getCreatedAt());
        return m;
    }

    private static Integer safeInt(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return null; }
    }
}
