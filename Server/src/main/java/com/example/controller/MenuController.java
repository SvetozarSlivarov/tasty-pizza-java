package com.example.controller;

import com.example.model.User;
import com.example.model.dto.PizzaDetails;
import com.example.model.enums.UserRole;
import com.example.network.SessionManager;
import com.example.service.MenuService;
import com.example.utils.JsonResponse;
import com.example.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

public class MenuController {

    private final MenuService menu = new MenuService();

    public JsonNode handle(String action, JsonNode payload) {
        return switch (action) {
            case "menu:pizzas"       -> listPizzas(payload);
            case "menu:drinks"       -> listDrinks(payload);
            case "menu:pizzaDetails" -> pizzaDetails(payload);
            default -> JsonResponse.error("Unknown menu action");
        };
    }

    // -------- actions --------

    private JsonNode listPizzas(JsonNode p) {
        UserRole role = roleFromToken(p);
        if (role == null) return JsonResponse.error("Unauthorized");
        return JsonResponse.success(
                "Pizzas",
                JsonUtil.mapper.valueToTree(menu.listPizzasFor(role))
        );
    }

    private JsonNode listDrinks(JsonNode p) {
        UserRole role = roleFromToken(p);
        if (role == null) return JsonResponse.error("Unauthorized");
        return JsonResponse.success(
                "Drinks",
                JsonUtil.mapper.valueToTree(menu.listDrinksFor(role))
        );
    }

    private JsonNode pizzaDetails(JsonNode p) {
        UserRole role = roleFromToken(p);
        if (role == null) return JsonResponse.error("Unauthorized");
        if (!p.has("pizzaId")) return JsonResponse.error("Missing pizzaId");

        int pizzaId = p.get("pizzaId").asInt();
        PizzaDetails details = menu.getPizzaDetails(pizzaId, role);
        if (details == null) return JsonResponse.error("Pizza not found or not available");

        return JsonResponse.success("Pizza details", JsonUtil.mapper.valueToTree(details));
    }

    // -------- helpers --------

    private UserRole roleFromToken(JsonNode p) {
        if (p == null || !p.has("authToken")) return null;
        User u = SessionManager.get(p.get("authToken").asText());
        return u != null ? u.getRole() : null;
    }
}
