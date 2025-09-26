package com.example.controller;

import com.example.dto.ingredient.IngredientCreateDto;
import com.example.http.HttpUtils;
import com.example.model.Ingredient;
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

    // GET /api/ingredients
    public void handleList(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "GET");
        String type = HttpUtils.queryParam(ex, "typeId");
        if (type != null && !type.isBlank()) {
            int typeId = Integer.parseInt(type);
            List<Ingredient> list = service.findByType(typeId);
            HttpUtils.sendJson(ex, 200, list.stream().map(this::toView).toList());
            return;
        }
        List<Ingredient> list = service.findAll();
        HttpUtils.sendJson(ex, 200, list.stream().map(this::toView).toList());
    }

    // POST /api/ingredients  (ADMIN) — body: { name, typeId }
    public void handleCreate(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);

        IngredientCreateDto body = JsonUtil.fromJson(HttpUtils.readBody(ex), IngredientCreateDto.class);
        Ingredient created = service.create(body.name, body.typeId);
        HttpUtils.sendJson(ex, 201, toView(created));
    }

    // PATCH /api/ingredients/{id}  (ADMIN) — body: { name?, typeId? }
    public void handleUpdate(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "PATCH");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);

        IngredientCreateDto body = JsonUtil.fromJson(HttpUtils.readBody(ex), IngredientCreateDto.class);
        Ingredient updated = service.update(id, body.name, body.typeId);
        HttpUtils.sendJson(ex, 200, toView(updated));
    }

    // DELETE /api/ingredients/{id}  (ADMIN)
    public void handleDelete(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "DELETE");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);

        service.delete(id);
        HttpUtils.sendStatus(ex, 204);
    }

    // === helper: (IngredientView) ===
    private Map<String,Object> toTypeView(Ingredient ing) {
        if (ing.getType() == null) return null;
        return Map.of("id", ing.getType().getId(), "name", ing.getType().getName());
    }
    private Map<String,Object> toView(Ingredient ing) {
        return Map.of(
                "id", ing.getId(),
                "name", ing.getName(),
                "type", toTypeView(ing)
        );
    }
}