package com.example.controller;

import com.example.dto.*;
import com.example.http.HttpUtils;
import com.example.model.Pizza;
import com.example.dao.impl.PizzaDetails;
import com.example.model.enums.UserRole;
import com.example.security.JwtService;
import com.example.service.PizzaService;
import com.example.utils.JsonUtil;
import com.example.utils.mapper.MenuMappers;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PizzaController {
    private final PizzaService pizzas;
    private final JwtService jwt;

    public PizzaController(PizzaService pizzas, JwtService jwt) {
        this.pizzas = pizzas; this.jwt = jwt;
    }

    // GET /api/pizzas?availableOnly=true
    public void handleList(HttpExchange ex) throws IOException {
        boolean availableOnly = "true".equalsIgnoreCase(HttpUtils.queryParam(ex, "availableOnly"));
        UserRole role = HttpUtils.roleOr(ex, UserRole.CUSTOMER, jwt);
        var list = pizzas.findAll(availableOnly, role).stream().map(p -> MenuMappers.toDto(p)).toList();
        HttpUtils.sendJson(ex, 200, list);
    }

    // POST /api/pizzas  (ADMIN)
    public void handleCreate(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        var req = JsonUtil.fromJson(HttpUtils.readBody(ex), PizzaCreateRequest.class);
        if (req.name() == null || req.price() == null) { HttpUtils.sendJson(ex, 400, Map.of("error","bad_request")); return; }
        Pizza created = pizzas.create(req);
        HttpUtils.sendJson(ex, 201, MenuMappers.toDto(created));
    }

    // GET /api/pizzas/{id}
    public void handleGet(HttpExchange ex, int id) throws IOException {
        var role = HttpUtils.roleOr(ex, UserRole.CUSTOMER, jwt);
        PizzaDetails details = pizzas.getDetails(id, role);
        if (details == null) { HttpUtils.sendJson(ex, 404, Map.of("error","not_found")); return; }
        HttpUtils.sendJson(ex, 200, details);
    }

    // PATCH /api/pizzas/{id}  (ADMIN)
    public void handleUpdate(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "PATCH");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        var req = JsonUtil.fromJson(HttpUtils.readBody(ex), PizzaUpdateRequest.class);
        try {
            var updated = pizzas.update(id, req);
            HttpUtils.sendJson(ex, 200, MenuMappers.toDto(updated));
        } catch (Exception e) {
            HttpUtils.sendJson(ex, 404, Map.of("error","not_found"));
        }
    }

    // DELETE /api/pizzas/{id}  (ADMIN)
    public void handleDelete(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "DELETE");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        try { pizzas.delete(id); HttpUtils.sendStatus(ex, 204); }
        catch (Exception e) { HttpUtils.sendJson(ex, 404, Map.of("error","not_found")); }
    }
    public void handleIngredientsList(com.sun.net.httpserver.HttpExchange ex, int pizzaId) throws java.io.IOException {
        HttpUtils.requireMethod(ex, "GET");
        List<PizzaIngredientView> list = pizzas.listPizzaIngredientsView(pizzaId);
        HttpUtils.sendJson(ex, 200, list);
    }

    public void handleIngredientAdd(com.sun.net.httpserver.HttpExchange ex, int pizzaId) throws java.io.IOException {
        HttpUtils.requireMethod(ex, "POST");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        PizzaIngredientAddRequest req = JsonUtil.fromJson(HttpUtils.readBody(ex), PizzaIngredientAddRequest.class);
        boolean ok = pizzas.addIngredientToPizza(pizzaId, req.ingredientId(), req.isRemovable());
        if (ok) {
            List<PizzaIngredientView> list = pizzas.listPizzaIngredientsView(pizzaId);
            HttpUtils.sendJson(ex, 201, list);
        } else {
            HttpUtils.sendJson(ex, 400, Map.of("error","cannot_add"));
        }
    }

    public void handleIngredientUpdate(com.sun.net.httpserver.HttpExchange ex, int pizzaId, int ingredientId) throws java.io.IOException {
        HttpUtils.requireMethod(ex, "PATCH");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        PizzaIngredientUpdateRequest req = JsonUtil.fromJson(HttpUtils.readBody(ex), PizzaIngredientUpdateRequest.class);
        boolean ok = pizzas.updateIngredientRemovability(pizzaId, ingredientId, req.isRemovable());
        if (ok){
            List<PizzaIngredientView> list = pizzas.listPizzaIngredientsView(pizzaId);
            HttpUtils.sendJson(ex, 204, list);
        } else {
            HttpUtils.sendJson(ex, 400, Map.of("error", "cannot_update"));
        }
    }

    public void handleIngredientDelete(com.sun.net.httpserver.HttpExchange ex, int pizzaId, int ingredientId) throws java.io.IOException {
        HttpUtils.requireMethod(ex, "DELETE");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        boolean ok = pizzas.removeIngredientFromPizza(pizzaId, ingredientId);
        if (ok) HttpUtils.sendStatus(ex, 204);
        else HttpUtils.sendJson(ex, 400, Map.of("error","cannot_delete"));
    }


    public void handleAllowedList(com.sun.net.httpserver.HttpExchange ex, int pizzaId) throws java.io.IOException {
        HttpUtils.requireMethod(ex, "GET");
        var allowed = pizzas.listAllowedIngredients(pizzaId);
        HttpUtils.sendJson(ex, 200, allowed);
    }

    public void handleAllowedAdd(com.sun.net.httpserver.HttpExchange ex, int pizzaId) throws java.io.IOException {
        HttpUtils.requireMethod(ex, "POST");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        PizzaAllowedAddRequest req = JsonUtil.fromJson(HttpUtils.readBody(ex), PizzaAllowedAddRequest.class);
        boolean ok = pizzas.allowIngredientForPizza(pizzaId, req.ingredientId());
        if (ok) HttpUtils.sendStatus(ex, 201);
        else HttpUtils.sendJson(ex, 400, Map.of("error","cannot_allow"));
    }

    public void handleAllowedDelete(com.sun.net.httpserver.HttpExchange ex, int pizzaId, int ingredientId) throws java.io.IOException {
        HttpUtils.requireMethod(ex, "DELETE");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        boolean ok = pizzas.disallowIngredientForPizza(pizzaId, ingredientId);
        if (ok) HttpUtils.sendStatus(ex, 204);
        else HttpUtils.sendJson(ex, 400, Map.of("error","cannot_disallow"));
    }
}