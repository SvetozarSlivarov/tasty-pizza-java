package com.example.controller;

import com.example.http.HttpUtils;
import com.example.model.Ingredient;
import com.example.model.IngredientType;
import com.example.model.enums.UserRole;
import com.example.security.JwtService;
import com.example.service.IngredientService;
import com.example.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class IngredientController {
    private final IngredientService service;
    private final JwtService jwt;

    public IngredientController(IngredientService service, JwtService jwt) {
        this.service = service; this.jwt = jwt;
    }

    // GET /api/ingredients?typeId=...
    public void handleList(HttpExchange ex) throws IOException {
        Integer typeId = HttpUtils.queryParamInt(ex, "typeId");
        List<Ingredient> res = (typeId != null) ? service.findByType(typeId) : service.findAll();
        HttpUtils.sendJson(ex, 200, res);
    }

    // POST /api/ingredients (ADMIN)
    public void handleCreate(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);

        var body = HttpUtils.readBody(ex);
        var dto = JsonUtil.fromJson(body, com.example.dto.IngredientCreateDto.class);

        if (dto == null || dto.name == null || dto.name.isBlank() || dto.typeId == null || dto.typeId <= 0) {
            HttpUtils.sendJson(ex, 400, java.util.Map.of("error", "bad_request"));
            return;
        }

        var created = service.create(dto.name, dto.typeId);
        HttpUtils.sendJson(ex, 201, created);
    }

    // PATCH /api/ingredients/{id} (ADMIN)
    public void handleUpdate(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "PATCH");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        Ingredient ing = JsonUtil.fromJson(HttpUtils.readBody(ex), Ingredient.class);
        HttpUtils.sendJson(ex, 200, service.update(id, ing));
    }

    // DELETE /api/ingredients/{id} (ADMIN)
    public void handleDelete(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "DELETE");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        service.delete(id);
        HttpUtils.sendStatus(ex, 204);
    }
}