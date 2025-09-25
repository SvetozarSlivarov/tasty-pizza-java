package com.example.controller;

import com.example.dao.OrderDao;
import com.example.dao.impl.OrderDaoImpl;
import com.example.http.HttpUtils;

import com.example.model.enums.UserRole;
import com.example.security.JwtService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class MaintenanceController {
    private final JwtService jwt;
    private final OrderDao orderDao = new OrderDaoImpl();

    public MaintenanceController(JwtService jwt){
        this.jwt = jwt;
    }
    public void handlePruneGuestCarts(HttpExchange ex) throws IOException {
        if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
            HttpUtils.methodNotAllowed(ex, "POST"); return;
        }
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        int days = 30;
        String q = ex.getRequestURI().getQuery();
        if (q != null) {
            for (String part : q.split("&")) {
                String[] kv = part.split("=", 2);
                if (kv.length == 2 && kv[0].equals("days")) {
                    try { days = Math.max(1, Integer.parseInt(kv[1])); } catch (NumberFormatException ignored) {}
                }
            }
        }
        Instant cutoff = Instant.now().minus(Duration.ofDays(days));
        int deleted = orderDao.deleteGuestCartsOlderThan(cutoff);

        HttpUtils.sendJson(ex, 200, Map.of(
                "deleted", deleted,
                "cutoff", cutoff.toString(),
                "days", days
        ));
    }
}
