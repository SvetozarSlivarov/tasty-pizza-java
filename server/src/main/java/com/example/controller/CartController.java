package com.example.controller;

import com.example.http.HttpUtils;
import com.example.security.JwtService;
import com.example.service.CartService;
import com.example.dto.*;
import com.sun.net.httpserver.HttpExchange;

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
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "GET"); return; }

        Integer userId = HttpUtils.tryGetUserId(ex, jwt);
        Integer cartIdHint = HttpUtils.tryGetCookieInt(ex, "cartId");

        int cartId = cart.ensureCart(userId, cartIdHint);

        if (cartIdHint == null || !cartIdHint.equals(cartId)) {
            HttpUtils.setCookie(ex, "cartId", String.valueOf(cartId), 60 * 60 * 24 * 30);
        }

        var view = cart.getCart(cartId);
        HttpUtils.sendJson(ex, 200, view);
    }

    // POST /api/cart/items/drink
    public void handleAddDrink(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        Integer userId = HttpUtils.tryGetUserId(ex, jwt);
        Integer cartId = HttpUtils.tryGetCookieInt(ex, "cartId");

        int orderId = cart.ensureCart(userId, cartId);
        ensureCartCookie(ex, cartId, orderId);

        AddDrinkToCartRequest req = HttpUtils.parseJson(ex, AddDrinkToCartRequest.class);
        var item = cart.addDrink(orderId, req.productId(), req.quantity(), req.note());
        HttpUtils.sendJson(ex, 201, Map.of("itemId", item.getId()));
    }

    // POST /api/cart/items/pizza
    public void handleAddPizza(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        Integer userId = HttpUtils.tryGetUserId(ex, jwt);
        Integer cartId = HttpUtils.tryGetCookieInt(ex, "cartId");

        int orderId = cart.ensureCart(userId, cartId);
        ensureCartCookie(ex, cartId, orderId);

        AddPizzaToCartRequest req = HttpUtils.parseJson(ex, AddPizzaToCartRequest.class);
        var item = cart.addPizza(
                orderId,
                req.productId(),
                req.variantId(),
                req.quantity(),
                req.note(),
                req.removeIngredientIds(),
                req.addIngredientIds()
        );
        HttpUtils.sendJson(ex, 201, Map.of("itemId", item.getId()));
    }

    // PATCH /api/cart/items/{id}
    public void handleUpdateItem(HttpExchange ex, int itemId) throws IOException {
        HttpUtils.requireMethod(ex, "PATCH");
        UpdateCartItemRequest req = HttpUtils.parseJson(ex, UpdateCartItemRequest.class);

        if (req.quantity() != null) cart.setQuantity(itemId, req.quantity());
        if (req.variantId() != null) cart.setVariant(itemId, req.variantId());
        if (req.note() != null) cart.setNote(itemId, req.note());

        HttpUtils.sendStatus(ex, 204);
    }

    // DELETE /api/cart/items/{id}
    public void handleDeleteItem(HttpExchange ex, int itemId) throws IOException {
        HttpUtils.requireMethod(ex, "DELETE");
        cart.removeItem(itemId);
        HttpUtils.sendStatus(ex, 204);
    }

    // POST /api/cart/checkout
    public void handleCheckout(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        Integer userId = HttpUtils.tryGetUserId(ex, jwt);
        Integer cartId = HttpUtils.tryGetCookieInt(ex, "cartId");

        int orderId = cart.ensureCart(userId, cartId);
        ensureCartCookie(ex, cartId, orderId);

        CheckoutRequest req = HttpUtils.parseJson(ex, CheckoutRequest.class);
        if (req == null || req.phone() == null || req.phone().isBlank())
            throw new IllegalArgumentException("phone_required");
        if (req.address() == null || req.address().isBlank())
            throw new IllegalArgumentException("address_required");

        cart.setDeliveryInfo(orderId, req.phone(), req.address());
        cart.checkout(orderId);

        HttpUtils.sendStatus(ex, 204);
    }

    private void ensureCartCookie(HttpExchange ex, Integer currentCookie, int orderId) {
        if (currentCookie == null || !currentCookie.equals(orderId)) {
            ex.getResponseHeaders().add("Set-Cookie",
                    "cartId=" + orderId + "; Path=/; HttpOnly; SameSite=Lax");
        }
    }
}
