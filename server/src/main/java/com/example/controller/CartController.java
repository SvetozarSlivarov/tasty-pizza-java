package com.example.controller;

import com.example.dto.cart.CartView;
import com.example.dto.cart.*;
import com.example.security.JwtService;
import com.example.service.CartService;
import com.example.http.HttpUtils;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CartController {
    private final CartService cart;
    private final JwtService jwt;

    public CartController(CartService cart, JwtService jwt) {
        this.cart = cart;
        this.jwt = jwt;
    }

    // ===== GET /cart =====
    public void getCart(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "GET");
        Integer userId = HttpUtils.tryGetUserId(ex, jwt);
        Integer cartId = HttpUtils.tryGetCookieInt(ex, "cartId");

        int orderId = cart.ensureCart(userId, cartId);
        ensureCartCookie(ex, cartId, orderId);

        CartView view = cart.getCart(orderId);
        HttpUtils.sendJson(ex, 200, view);
    }

    // ===== POST /cart/items/drink =====
    public void addDrink(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        Integer userId = HttpUtils.tryGetUserId(ex, jwt);
        Integer cartId = HttpUtils.tryGetCookieInt(ex, "cartId");
        int orderId = cart.ensureCart(userId, cartId);
        ensureCartCookie(ex, cartId, orderId);

        var req = HttpUtils.parseJson(ex, AddDrinkRequest.class);
        if (req == null) {
            HttpUtils.sendJsonError(ex, 400, "bad_request", "Invalid body.", null);
            return;
        }
        try {
            cart.addDrink(orderId, req.productId(), req.qty(), req.note());
            HttpUtils.sendStatus(ex, 204);
        } catch (IllegalArgumentException iae) {
            HttpUtils.sendJsonError(ex, 400, iae.getMessage(), humanMessage(iae.getMessage()), null);
        }
    }

    // ===== POST /cart/items/pizza =====
    public void addPizza(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        Integer userId = HttpUtils.tryGetUserId(ex, jwt);
        Integer cartId = HttpUtils.tryGetCookieInt(ex, "cartId");
        int orderId = cart.ensureCart(userId, cartId);
        ensureCartCookie(ex, cartId, orderId);

        var req = HttpUtils.parseJson(ex, AddPizzaRequest.class);
        if (req == null) {
            HttpUtils.sendJsonError(ex, 400, "bad_request", "Invalid body.", null);
            return;
        }
        try {
            cart.addPizza(orderId, req.productId(), req.variantId(), req.qty(),
                    req.note(), req.removeIds(), req.addIds());
            HttpUtils.sendStatus(ex, 204);
        } catch (IllegalArgumentException iae) {
            String code = iae.getMessage();
            int status = switch (code) {
                case "qty_invalid", "variant_invalid",
                        "ingredient_in_both_add_and_remove",
                        "remove_not_in_base", "remove_not_removable" -> 400;
                case "pizza_not_found" -> 404;
                case "add_not_allowed", "addon_unavailable" -> 409;
                default -> 400;
            };
            HttpUtils.sendJsonError(ex, status, code, humanMessage(code),
                    Map.of("addIds", req.addIds(), "removeIds", req.removeIds()));
        }
    }

    // ===== PATCH /cart/items/{id} =====
    public void handleUpdateItem(HttpExchange ex, int itemId) throws IOException {
        HttpUtils.requireMethod(ex, "PATCH");

        // Parse as generic map to detect which field is being updated
        Map<String, Object> body = HttpUtils.parseJsonMap(ex);
        if (body == null || body.isEmpty()) {
            HttpUtils.sendJsonError(ex, 400, "bad_request", "Invalid body.", null);
            return;
        }

        try {
            if (body.containsKey("qty")) {
                int qty = ((Number) body.get("qty")).intValue();
                cart.setQuantity(itemId, qty);
            }
            if (body.containsKey("variantId")) {
                Integer variantId = body.get("variantId") != null
                        ? ((Number) body.get("variantId")).intValue()
                        : null;
                cart.setVariant(itemId, variantId);
            }
            if (body.containsKey("note")) {
                String note = (String) body.get("note");
                cart.setNote(itemId, note);
            }
            if (body.containsKey("removeIds") || body.containsKey("addIds")) {
                List<Integer> removeIds = body.containsKey("removeIds")
                        ? (List<Integer>) body.get("removeIds") : List.of();
                List<Integer> addIds = body.containsKey("addIds")
                        ? (List<Integer>) body.get("addIds") : List.of();
                cart.replacePizzaCustomizations(itemId, removeIds, addIds);
            }
            HttpUtils.sendStatus(ex, 204);
        } catch (IllegalArgumentException iae) {
            String code = iae.getMessage();
            HttpUtils.sendJsonError(ex, 400, code, humanMessage(code), null);
        }
    }

    // ===== DELETE /cart/items/{id} =====
    public void handleDeleteItem(HttpExchange ex, int itemId) throws IOException {
        HttpUtils.requireMethod(ex, "DELETE");
        try {
            cart.removeItem(itemId);
            HttpUtils.sendStatus(ex, 204);
        } catch (IllegalStateException ise) {
            HttpUtils.sendJsonError(ex, 500, "delete_failed", "Failed to delete item.", null);
        }
    }

    // ===== POST /cart/checkout =====
    public void handleCheckout(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        Integer userId = HttpUtils.tryGetUserId(ex, jwt);
        Integer cartId = HttpUtils.tryGetCookieInt(ex, "cartId");
        int orderId = cart.ensureCart(userId, cartId);
        ensureCartCookie(ex, cartId, orderId);

        var req = HttpUtils.parseJson(ex, CheckoutRequest.class);
        if (req == null) {
            HttpUtils.sendJsonError(ex, 400, "bad_request", "Invalid JSON body.", null);
            return;
        }

        String phoneRaw = req.phone();
        if (phoneRaw == null || phoneRaw.isBlank()) {
            HttpUtils.sendJsonError(ex, 400, "phone_required", "Phone number is required.", null);
            return;
        }
        String phone = phoneRaw.replaceAll("\\s+", "");
        if (!phone.matches("^(?:\\+359|0)8[7-9]\\d{7}$")) {
            HttpUtils.sendJsonError(
                    ex, 400,
                    "invalid_phone",
                    "Enter a valid Bulgarian mobile number.",
                    Map.of("example", "+35988XXXXXXX or 088XXXXXXX")
            );
            return;
        }

        String addressRaw = req.address();
        if (addressRaw == null || addressRaw.isBlank()) {
            HttpUtils.sendJsonError(ex, 400, "address_required", "Address is required.", null);
            return;
        }
        String address = addressRaw.trim().replaceAll("\\s{2,}", " ");
        int len = address.length();
        if (len < 5 || len > 200) {
            HttpUtils.sendJsonError(
                    ex, 400,
                    "invalid_address",
                    len < 5 ? "Address is too short." : "Address is too long.",
                    Map.of("min", 5, "max", 200)
            );
            return;
        }
        if (address.matches(".*\\p{Cntrl}.*")) {
            HttpUtils.sendJsonError(
                    ex, 400,
                    "invalid_address",
                    "Address contains control characters.",
                    null
            );
            return;
        }
        var issues = cart.validateForCheckout(orderId);
        if (!issues.isEmpty()) {
            HttpUtils.sendJsonError(
                    ex, 409,
                    "cart_invalid",
                    "The cart contains invalid or unavailable items.",
                    Map.of("issues", issues)
            );
            return;
        }
        cart.setDeliveryInfo(orderId, phone, address);
        cart.checkout(orderId);
        HttpUtils.sendJson(ex, 200, Map.of(
                "status", "ok",
                "orderId", orderId
        ));
    }

    // ===== Helpers =====
    private void ensureCartCookie(HttpExchange ex, Integer currentCookieCartId, int ensured) {
        if (currentCookieCartId == null || !currentCookieCartId.equals(ensured)) {
            HttpUtils.setCookie(ex, "cartId", String.valueOf(ensured), 60 * 60 * 24 * 30);
        }
    }

    private static String humanMessage(String code) {
        if (code == null) return "Invalid operation.";
        return switch (code) {
            case "qty_invalid" -> "Invalid quantity.";
            case "variant_invalid" -> "Invalid pizza variant.";
            case "ingredient_in_both_add_and_remove" -> "The same ingredient cannot be both added and removed.";
            case "remove_not_in_base" -> "This ingredient is not in the base recipe.";
            case "remove_not_removable" -> "This ingredient cannot be removed.";
            case "pizza_not_found" -> "Pizza not found or unavailable.";
            case "add_not_allowed" -> "This ingredient is not allowed for the selected pizza.";
            case "addon_unavailable" -> "This ingredient is no longer available.";
            case "item_not_found" -> "Cart item not found.";
            case "not_pizza_item", "not_a_pizza" -> "This cart item is not a pizza.";
            case "phone_required" -> "Phone number is required.";
            case "address_required" -> "Address is required.";
            case "cart_invalid" -> "The cart contains invalid or unavailable items.";
            default -> code;
        };
    }
}
