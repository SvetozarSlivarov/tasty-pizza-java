package com.example.controller;

import com.example.dto.image.ImageUploadRequest;
import com.example.dto.ingredient.IngredientTypeView;
import com.example.dto.ingredient.IngredientView;
import com.example.dto.ingredient.PizzaAllowedAddRequest;
import com.example.dto.pizza.PizzaDto;
import com.example.dto.pizza.PizzaIngredientAddRequest;
import com.example.dto.pizza.PizzaIngredientUpdateRequest;
import com.example.dto.pizza.PizzaIngredientView;
import com.example.http.HttpUtils;
import com.example.model.Ingredient;
import com.example.model.enums.UserRole;
import com.example.security.JwtService;
import com.example.service.PizzaService;
import com.example.service.PizzaIngredientService;
import com.example.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PizzaController {

    private final PizzaService pizzaService;
    private final PizzaIngredientService ingredientService;
    private final JwtService jwt;

    public PizzaController(PizzaService pizzaService, PizzaIngredientService ingredientService, JwtService jwt) {
        this.pizzaService = pizzaService;
        this.ingredientService = ingredientService;
        this.jwt = jwt;
    }

    // GET /pizzas  (?all=true, ?withVariants=true)
    public void handleList(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "GET");
        Map<String, String> q = HttpUtils.parseQuery(ex);
        boolean withVariants = "true".equalsIgnoreCase(q.getOrDefault("withVariants", "false"));

        boolean all = "true".equalsIgnoreCase(q.getOrDefault("all", "false"));
        boolean onlyAvailable = true;
        if (all) {
            HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
            onlyAvailable = false;
        }

        var list = pizzaService.list(onlyAvailable, withVariants);
        HttpUtils.sendJson(ex, 200, list);
    }

    // POST /pizzas   (ADMIN)
    public void handleCreate(HttpExchange ex) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);

        PizzaDto dto = JsonUtil.fromJson(HttpUtils.readBody(ex), PizzaDto.class);
        PizzaDto created = pizzaService.create(dto);
        HttpUtils.sendJson(ex, 201, created);
    }

    // GET /pizzas/{id}  (?withVariants=true)
    public void handleGet(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "GET");
        Map<String, String> q = HttpUtils.parseQuery(ex);
        boolean withVariants = "true".equalsIgnoreCase(q.getOrDefault("withVariants", "false"));

        PizzaDto dto = pizzaService.get(id, withVariants);
        HttpUtils.sendJson(ex, 200, dto);
    }

    // PATCH /pizzas/{id}   (ADMIN) — body: PizzaDto
    public void handleUpdate(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "PATCH");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);

        PizzaDto body = JsonUtil.fromJson(HttpUtils.readBody(ex), PizzaDto.class);
        PizzaDto toUpdate = new PizzaDto(
                body.id() != null ? body.id() : id,
                body.name(),
                body.description(),
                body.basePrice(),
                body.isAvailable(),
                body.spicyLevel(),
                body.imageUrl(),
                body.variants()
        );

        PizzaDto updated = pizzaService.update(toUpdate);
        HttpUtils.sendJson(ex, 200, updated);
    }
    // POST /pizzas/{id}/image   (ADMIN)
    public void handleUploadImage(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        var req = JsonUtil.fromJson(HttpUtils.readBody(ex), ImageUploadRequest.class);
        var dto = pizzaService.uploadImage(id, req);
        HttpUtils.sendJson(ex, 200, Map.of("id", dto.id(), "imageUrl", dto.imageUrl()));
    }

    // DELETE /pizzas/{id}   (ADMIN)
    public void handleDelete(HttpExchange ex, int id) throws IOException {
        HttpUtils.requireMethod(ex, "DELETE");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);

        pizzaService.delete(id);
        HttpUtils.sendStatus(ex, 204);
    }

    // ================== BASE INGREDIENTS ==================

    // GET /pizzas/{id}/ingredients
    public void handleIngredientsList(HttpExchange ex, int pizzaId) throws IOException {
        HttpUtils.requireMethod(ex, "GET");
        List<PizzaIngredientView> out = ingredientService.listPizzaIngredientsView(pizzaId);
        HttpUtils.sendJson(ex, 200, out);
    }

    // POST /pizzas/{id}/ingredients   (ADMIN) — body: PizzaIngredientAddRequest
    public void handleIngredientAdd(HttpExchange ex, int pizzaId) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        PizzaIngredientAddRequest req = JsonUtil.fromJson(HttpUtils.readBody(ex), PizzaIngredientAddRequest.class);
        boolean ok = ingredientService.addIngredientToPizza(pizzaId, req.ingredientId(), req.isRemovable());
        if (ok) HttpUtils.sendStatus(ex, 201);
        else HttpUtils.sendJson(ex, 400, Map.of("error","cannot_add"));
    }

    // PATCH /pizzas/{id}/ingredients/{ingredientId}  (ADMIN) — body: PizzaIngredientUpdateRequest
    public void handleIngredientUpdate(HttpExchange ex, int pizzaId, int ingredientId) throws IOException {
        HttpUtils.requireMethod(ex, "PATCH");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        PizzaIngredientUpdateRequest req = JsonUtil.fromJson(HttpUtils.readBody(ex), PizzaIngredientUpdateRequest.class);
        boolean ok = ingredientService.updateIngredientRemovability(pizzaId, ingredientId, req.isRemovable());
        if (ok) HttpUtils.sendStatus(ex, 204);
        else HttpUtils.sendJson(ex, 400, Map.of("error","cannot_update"));
    }

    // DELETE /pizzas/{id}/ingredients/{ingredientId}  (ADMIN)
    public void handleIngredientDelete(HttpExchange ex, int pizzaId, int ingredientId) throws IOException {
        HttpUtils.requireMethod(ex, "DELETE");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        boolean ok = ingredientService.removeIngredientFromPizza(pizzaId, ingredientId);
        if (ok) HttpUtils.sendStatus(ex, 204);
        else HttpUtils.sendJson(ex, 400, Map.of("error","cannot_remove"));
    }

    // ================== ALLOWED INGREDIENTS ==================

    // GET /pizzas/{id}/allowed-ingredients
    public void handleAllowedList(HttpExchange ex, int pizzaId) throws IOException {
        HttpUtils.requireMethod(ex, "GET");
        var allowed = ingredientService.listAllowedIngredients(pizzaId)
                .stream()
                .map(this::toIngredientView)
                .toList();
        HttpUtils.sendJson(ex, 200, allowed);
    }

    // POST /pizzas/{id}/allowed-ingredients
    public void handleAllowedAdd(HttpExchange ex, int pizzaId) throws IOException {
        HttpUtils.requireMethod(ex, "POST");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        PizzaAllowedAddRequest req = JsonUtil.fromJson(HttpUtils.readBody(ex), PizzaAllowedAddRequest.class);
        boolean ok = ingredientService.allowIngredientForPizza(pizzaId, req.ingredientId());
        if (ok) HttpUtils.sendStatus(ex, 201);
        else HttpUtils.sendJson(ex, 400, Map.of("error","cannot_allow"));
    }

    // DELETE /pizzas/{id}/allowed-ingredients/{ingredientId}   (ADMIN)
    public void handleAllowedDelete(HttpExchange ex, int pizzaId, int ingredientId) throws IOException {
        HttpUtils.requireMethod(ex, "DELETE");
        HttpUtils.requireRole(ex, jwt, UserRole.ADMIN);
        boolean ok = ingredientService.disallowIngredientForPizza(pizzaId, ingredientId);
        if (ok) HttpUtils.sendStatus(ex, 204);
        else HttpUtils.sendJson(ex, 400, Map.of("error","cannot_disallow"));
    }

    // ===== helpers =====
    private IngredientView toIngredientView(Ingredient ing) {
        IngredientTypeView type = null;
        if (ing.getType() != null) {
            type = new IngredientTypeView(ing.getType().getId(), ing.getType().getName());
        }
        return new IngredientView(ing.getId(), ing.getName(), type);
    }
}
