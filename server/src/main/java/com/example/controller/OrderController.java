package com.example.controller;

import com.example.dto.CartView;
import com.example.http.HttpUtils;
import com.example.model.enums.UserRole;
import com.example.security.JwtService;
import com.example.service.CartService;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class OrderController {

    private final CartService cart;
    private final JwtService jwt;

    public OrderController(CartService cart, JwtService jwt) {
        this.cart = cart;
        this.jwt = jwt;
    }

    public void handleGetOne(HttpExchange ex, int orderId) throws IOException {
        HttpUtils.requireMethod(ex, "GET");
        CartView view = cart.getCart(orderId);
        HttpUtils.sendJson(ex, 200, view);
    }

    // POST /api/orders/{id}/start-preparing
    public void handleStartPreparing(HttpExchange ex, int orderId) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        requireAdmin(ex);
        cart.startPreparing(orderId);             // ORDERED -> PREPARING (+ preparing_at)
        HttpUtils.sendStatus(ex, 204);
    }

    // POST /api/orders/{id}/out-for-delivery
    public void handleOutForDelivery(HttpExchange ex, int orderId) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        requireAdmin(ex);
        cart.outForDelivery(orderId);             // PREPARING -> OUT_FOR_DELIVERY (+ out_for_delivery_at)
        HttpUtils.sendStatus(ex, 204);
    }

    // POST /api/orders/{id}/deliver
    public void handleDeliver(HttpExchange ex, int orderId) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        requireAdmin(ex);
        cart.deliver(orderId);                    // OUT_FOR_DELIVERY -> DELIVERED (+ delivered_at)
        HttpUtils.sendStatus(ex, 204);
    }

    // POST /api/orders/{id}/cancel
    public void handleCancel(HttpExchange ex, int orderId) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        requireAdmin(ex);
        cart.cancel(orderId);                     // * -> CANCELLED (+ cancelled_at)
        HttpUtils.sendStatus(ex, 204);
    }

    private void requireAdmin(HttpExchange ex) throws IOException {
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
    }
}
