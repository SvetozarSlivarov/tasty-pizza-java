package com.example.controller;

import com.example.dto.ingredient.IngredientTypeCreateDto;
import com.example.http.HttpUtils;
import com.example.model.IngredientType;
import com.example.model.enums.UserRole;
import com.example.security.JwtService;
import com.example.service.IngredientService;
import com.example.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;

public class IngredientTypeController {
    private final IngredientService service;
    private final JwtService jwt;

    public IngredientTypeController(IngredientService service, JwtService jwt) {
        this.service = service;
        this.jwt = jwt;
    }

    // GET /api/ingredient-types
    public void handleList(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "GET");
        List<IngredientType> items = service.types();
        HttpUtils.sendJson(ex, 200, items);
    }

    // POST /api/ingredient-types (ADMIN)
    public void handleCreate(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);

        var body = HttpUtils.readBody(ex);
        IngredientTypeCreateDto payload = JsonUtil.fromJson(body, IngredientTypeCreateDto.class);

        String name = payload != null ? payload.name : null;
        IngredientType created = service.createType(name);

        HttpUtils.sendJson(ex, 201, created);
    }

    // PATCH /api/ingredient-types/{id} (ADMIN)
    public void handleUpdate(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "PATCH");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        IngredientType t = JsonUtil.fromJson(HttpUtils.readBody(ex), IngredientType.class);
        t.setId(id);
        boolean ok = new com.example.dao.impl.IngredientTypeDaoImpl().update(t);
        if (!ok) throw new com.example.exception.NotFoundException("ingredient_type_not_found");
        HttpUtils.sendJson(ex, 200, t);
    }

    // DELETE /api/ingredient-types/{id} (ADMIN)
    public void handleDelete(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "DELETE");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        boolean ok = new com.example.dao.impl.IngredientTypeDaoImpl().delete(id);
        if (!ok) throw new com.example.exception.NotFoundException("ingredient_type_not_found");
        HttpUtils.sendStatus(ex, 204);
    }
}