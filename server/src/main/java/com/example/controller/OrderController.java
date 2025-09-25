package com.example.controller;

import com.example.dao.OrderDao;
import com.example.dto.cart.CartView;
import com.example.http.HttpUtils;
import com.example.model.enums.UserRole;
import com.example.security.JwtService;
import com.example.service.CartService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

public class OrderController {

    private final CartService cart;
    private final JwtService jwt;

    private final OrderDao orderDao;

    public OrderController(CartService cart, JwtService jwt, OrderDao  orderDao) {
        this.cart = cart;
        this.jwt = jwt;
        this.orderDao = orderDao;
    }

    public void handleGetOne(HttpExchange ex, int orderId) throws IOException {
        HttpUtils.requireMethod(ex, "GET");
        CartView view = cart.getCart(orderId);
        HttpUtils.sendJson(ex, 200, view);
    }
    public void handleReorder(HttpExchange ex, int orderId) throws IOException {
        HttpUtils.requireMethod(ex, "POST");

        Integer userId = HttpUtils.tryGetUserId(ex, jwt);
        if (userId == null || userId <= 0) { HttpUtils.sendJson(ex, 401, Map.of("error","unauthorized")); return; }

        var order = orderDao.findById(orderId);
        if (order == null) { HttpUtils.sendJson(ex, 404, Map.of("error","order_not_found")); return; }
        if (order.getUserId() == null || order.getUserId() != userId) { HttpUtils.sendJson(ex, 403, Map.of("error","forbidden")); return; }
        if (order.getStatus() == com.example.model.enums.OrderStatus.CART) { HttpUtils.sendJson(ex, 400, Map.of("error","cannot_reorder_cart")); return; }

        Integer cartIdHint = HttpUtils.tryGetCookieInt(ex, "cartId");
        int cartId = cart.ensureCart(userId, cartIdHint);
        HttpUtils.setCookie(ex, "cartId", String.valueOf(cartId), 60 * 60 * 24 * 30);

        try {
            cart.importFromOrder(orderId, cartId);
        } catch (IllegalArgumentException iae) {
            HttpUtils.sendJson(ex, 400, Map.of("error", iae.getMessage())); return;
        }

        var cv = cart.getCart(cartId);
        HttpUtils.sendJson(ex, 201, cv);
    }

    // POST /api/orders/{id}/start-preparing
    public void handleStartPreparing(HttpExchange ex, int orderId) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        requireAdmin(ex);
        cart.startPreparing(orderId);
        HttpUtils.sendStatus(ex, 204);
    }

    // POST /api/orders/{id}/out-for-delivery
    public void handleOutForDelivery(HttpExchange ex, int orderId) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        requireAdmin(ex);
        cart.outForDelivery(orderId);
        HttpUtils.sendStatus(ex, 204);
    }

    // POST /api/orders/{id}/deliver
    public void handleDeliver(HttpExchange ex, int orderId) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        requireAdmin(ex);
        cart.deliver(orderId);
        HttpUtils.sendStatus(ex, 204);
    }

    // POST /api/orders/{id}/cancel
    public void handleCancel(HttpExchange ex, int orderId) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        requireAdmin(ex);
        cart.cancel(orderId);
        HttpUtils.sendStatus(ex, 204);
    }

    private void requireAdmin(HttpExchange ex) throws IOException {
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
    }
}
