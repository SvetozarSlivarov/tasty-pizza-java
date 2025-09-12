package com.example.controller;

import com.example.http.HttpUtils;
import com.example.security.JwtService;
import com.example.service.CartService;
import com.example.dto.CartView;
import com.example.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.example.dto.*;

import java.io.IOException;
import java.util.Map;

public class CartController {
    private final CartService cart;
    private final JwtService jwt;

    public CartController(CartService cart, JwtService jwt) {
        this.cart = cart;
        this.jwt = jwt;
    }

    // GET /api/cart
    public void handleGet(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "GET");
        Integer userId = HttpUtils.tryGetUserId(ex, jwt);
        Integer cartId = HttpUtils.tryGetCookieInt(ex, "cartId");

        int orderId = cart.ensureCart(userId, cartId);
        ensureCartCookie(ex, cartId, orderId);

        CartView view = cart.getCart(orderId);
        HttpUtils.sendJson(ex, 200, view);
    }

    // POST /api/cart/items/pizza
    public void handleAddPizza(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        Integer userId = HttpUtils.tryGetUserId(ex, jwt);
        Integer cartId = HttpUtils.tryGetCookieInt(ex, "cartId");
        int orderId = cart.ensureCart(userId, cartId);
        ensureCartCookie(ex, cartId, orderId);

        var req = JsonUtil.fromJson(HttpUtils.readBody(ex), AddPizzaToCartRequest.class);
        var item = cart.addPizza(orderId, req.productId(), req.variantId(), req.quantity(), req.note(),
                req.removeIngredientIds(), req.addIngredientIds());
        HttpUtils.sendJson(ex, 201, Map.of("itemId", item.getId()));
    }

    // POST /api/cart/items/drink
    public void handleAddDrink(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        Integer userId = HttpUtils.tryGetUserId(ex, jwt);
        Integer cartId = HttpUtils.tryGetCookieInt(ex, "cartId");
        int orderId = cart.ensureCart(userId, cartId);
        ensureCartCookie(ex, cartId, orderId);

        var req = JsonUtil.fromJson(HttpUtils.readBody(ex), AddDrinkToCartRequest.class);
        var item = cart.addDrink(orderId, req.productId(), req.quantity(), req.note());
        HttpUtils.sendJson(ex, 201, Map.of("itemId", item.getId()));
    }

    // PATCH /api/cart/items/{id}
    public void handleUpdateItem(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "PATCH");
        var req = JsonUtil.fromJson(HttpUtils.readBody(ex), UpdateCartItemRequest.class);
        if (req.quantity() != null) cart.setQuantity(id, req.quantity());
        if (req.variantId() != null) cart.setVariant(id, req.variantId());
        if (req.note() != null) cart.setNote(id, req.note());
        HttpUtils.sendStatus(ex, 204);
    }

    // DELETE /api/cart/items/{id}
    public void handleDeleteItem(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "DELETE");
        cart.removeItem(id);
        HttpUtils.sendStatus(ex, 204);
    }

    // POST /api/cart/checkout
    public void handleCheckout(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        Integer userId = HttpUtils.tryGetUserId(ex, jwt);
        Integer cartId = HttpUtils.tryGetCookieInt(ex, "cartId");
        int orderId = cart.ensureCart(userId, cartId);
        ensureCartCookie(ex, cartId, orderId);
        cart.checkout(orderId);
        HttpUtils.sendStatus(ex, 204);
    }

    private void ensureCartCookie(HttpExchange ex, Integer currentCookie, int orderId) {
        if (currentCookie == null || currentCookie != orderId) {
            ex.getResponseHeaders().add("Set-Cookie", "cartId=" + orderId + "; Path=/; HttpOnly; SameSite=Lax");
        }
    }
}
