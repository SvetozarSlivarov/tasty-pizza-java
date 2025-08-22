package com.example.controller;

import com.example.dto.*;
import com.example.http.HttpUtils;
import com.example.model.Pizza;
import com.example.model.dto.PizzaDetails;
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

    // PUT /api/pizzas/{id}/ingredients  (replace base)
    public void handleReplaceBase(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "PUT");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        var req = JsonUtil.fromJson(HttpUtils.readBody(ex), PizzaCompositionRequest.class);
        pizzas.replaceBaseIngredients(id, req.ingredientIds(), /*requireSubsetOfAllowed*/ true);
        HttpUtils.sendStatus(ex, 204);
    }

    // PUT /api/pizzas/{id}/allowed-ingredients  (replace allowed)
    public void handleReplaceAllowed(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "PUT");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        var req = JsonUtil.fromJson(HttpUtils.readBody(ex), PizzaCompositionRequest.class);
        pizzas.replaceAllowedIngredients(id, req.ingredientIds(), /*enforceSupersetOfBase*/ true);
        HttpUtils.sendStatus(ex, 204);
    }
}