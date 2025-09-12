package com.example.controller;

import com.example.dto.DrinkCreateRequest;
import com.example.dto.DrinkResponse;
import com.example.dto.DrinkUpdateRequest;
import com.example.dto.ImageUploadRequest;
import com.example.http.HttpUtils;
import com.example.model.Drink;
import com.example.model.enums.UserRole;
import com.example.security.JwtService;
import com.example.service.DrinkService;
import com.example.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DrinkController {

    private final DrinkService drinks;
    private final JwtService jwt;

    public DrinkController(DrinkService drinks, JwtService jwt) {
        this.drinks = drinks;
        this.jwt = jwt;
    }

    // GET /api/drinks?availableOnly=true
    public void handleList(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "GET");
        var role = HttpUtils.resolveRole(ex, jwt);
        var q = HttpUtils.parseQuery(ex);
        boolean availableOnly = "true".equalsIgnoreCase(q.getOrDefault("availableOnly", "false"));

        List<Drink> list = drinks.findAll(availableOnly, role);
        var resp = list.stream()
                .map(d -> new DrinkResponse(
                        d.getId(),
                        d.getName(),
                        d.getDescription(),
                        d.getPrice(),
                        d.isAvailable(),
                        d.getImageUrl()
                ))
                .toList();

        HttpUtils.sendJson(ex, 200, resp);
    }

    // GET /api/drinks/{id}
    public void handleGet(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "GET");
        Drink d = drinks.findById(id);
        HttpUtils.sendJson(ex, 200, new DrinkResponse(
                d.getId(), d.getName(), d.getDescription(), d.getPrice(), d.isAvailable(), d.getImageUrl()
        ));
    }

    // POST /api/drinks   (ADMIN)
    public void handleCreate(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);

        DrinkCreateRequest req = JsonUtil.fromJson(HttpUtils.readBody(ex), DrinkCreateRequest.class);
        Drink d = drinks.create(req);

        HttpUtils.sendJson(ex, 201, new DrinkResponse(
                d.getId(), d.getName(), d.getDescription(), d.getPrice(), d.isAvailable(), d.getImageUrl()
        ));
    }

    // PATCH /api/drinks/{id}   (ADMIN)
    public void handleUpdate(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "PATCH");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);

        DrinkUpdateRequest req = JsonUtil.fromJson(HttpUtils.readBody(ex), DrinkUpdateRequest.class);
        Drink d = drinks.update(id, req);

        HttpUtils.sendJson(ex, 200, new DrinkResponse(
                d.getId(), d.getName(), d.getDescription(), d.getPrice(), d.isAvailable(), d.getImageUrl()
        ));
    }
    // POST /drinks/{id}/image   (ADMIN)
    public void handleUploadImage(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        var req = JsonUtil.fromJson(HttpUtils.readBody(ex), ImageUploadRequest.class);
        var d = drinks.uploadImage(id, req);
        HttpUtils.sendJson(ex, 200, Map.of("id", d.getId(), "imageUrl", d.getImageUrl()));
    }
    public void handleUpdateImageUrl(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "PATCH");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);

        record ImageUrlBody(String url) {}
        ImageUrlBody b = JsonUtil.fromJson(HttpUtils.readBody(ex), ImageUrlBody.class);

        var updated = drinks.update(id, new DrinkUpdateRequest(
                null, null, null, null, b.url()
        ));

        HttpUtils.sendJson(ex, 200, Map.of("id", updated.getId(), "imageUrl", updated.getImageUrl()));
    }

    // DELETE /api/drinks/{id}  (ADMIN)
    public void handleDelete(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "DELETE");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);

        drinks.delete(id);
        HttpUtils.sendStatus(ex, 204);
    }
}
