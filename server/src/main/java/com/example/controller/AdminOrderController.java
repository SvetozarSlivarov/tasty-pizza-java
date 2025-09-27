package com.example.controller;

import com.example.dto.order.AdminOrderDetailView;
import com.example.http.HttpUtils;
import com.example.model.enums.UserRole;
import com.example.security.JwtService;
import com.example.service.OrderQueryService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;

public class AdminOrderController {
    private final OrderQueryService queries;
    private final JwtService jwt;

    public AdminOrderController(OrderQueryService queries, JwtService jwt) {
        this.queries = queries;
        this.jwt = jwt;
    }

    public void handleList(HttpExchange ex) throws IOException {
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            HttpUtils.methodNotAllowed(ex, "GET");
            return;
        }
        UserRole role = HttpUtils.resolveRole(ex, jwt);
        if (role != UserRole.ADMIN) {
            HttpUtils.sendJson(ex, 403, Map.of("error", "forbidden"));
            return;
        }

        String status = HttpUtils.queryParam(ex, "status");
        String q      = HttpUtils.queryParam(ex, "q");
        Integer page  = HttpUtils.queryParamInt(ex, "page");
        Integer size  = HttpUtils.queryParamInt(ex, "size");
        String fromS  = HttpUtils.queryParam(ex, "from");
        String toS    = HttpUtils.queryParam(ex, "to");

        int limit  = (size == null || size <= 0 || size > 200) ? 50 : size;
        int offset = ((page == null || page < 1) ? 0 : (page - 1) * limit);

        Timestamp from = null, to = null;
        try {
            if (fromS != null && !fromS.isBlank()) from = Timestamp.valueOf(fromS + " 00:00:00");
            if (toS   != null && !toS.isBlank())   to   = Timestamp.valueOf(toS   + " 23:59:59");
        } catch (Exception ignore) {}

        var items = queries.listOrders(status, q, from, to, limit, offset);
        HttpUtils.sendJson(ex, 200, Map.of("items", items));
    }

    public void handleGet(HttpExchange ex, int id) throws IOException {
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            HttpUtils.methodNotAllowed(ex, "GET");
            return;
        }
        UserRole role = HttpUtils.resolveRole(ex, jwt);
        if (role != UserRole.ADMIN) {
            HttpUtils.sendJson(ex, 403, Map.of("error", "forbidden"));
            return;
        }

        AdminOrderDetailView view = queries.getOrder(id);
        if (view == null) {
            HttpUtils.sendJson(ex, 404, Map.of("error", "order_not_found"));
            return;
        }
        HttpUtils.sendJson(ex, 200, view);
    }
}
