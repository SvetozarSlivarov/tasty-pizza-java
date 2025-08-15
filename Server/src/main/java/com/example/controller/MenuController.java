// src/com/example/controller/MenuController.java
package com.example.controller;

import com.example.dto.*;
import com.example.exception.BadRequestException;
import com.example.http.HttpUtils;
import com.example.model.Drink;
import com.example.model.Pizza;
import com.example.model.dto.PizzaDetails;
import com.example.model.enums.UserRole;
import com.example.service.MenuService;
import com.example.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

public class MenuController {
    private final MenuService menu;

    public MenuController(MenuService menu){ this.menu = menu; }

    private UserRole role(HttpExchange ex) {
        Object r = ex.getAttribute("role");
        if (r instanceof String s) {
            try { return UserRole.valueOf(s.toUpperCase()); } catch (IllegalArgumentException ignored) {}
        }
        return UserRole.CUSTOMER;
    }

    public void listMenu(HttpExchange ex) throws IOException {
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
            HttpUtils.methodNotAllowed(ex, "GET"); return;
        }
        var payload = menu.listMenuFor(role(ex));
        HttpUtils.sendJson(ex, 200, payload);
    }
    public void listPizzas(HttpExchange ex) throws IOException {
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "GET"); return; }
        var pizzas = menu.listPizzasFor(role(ex));
        HttpUtils.sendJson(ex, 200, pizzas);
    }
    public void listDrinks(HttpExchange ex) throws IOException {
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "GET"); return; }
        var drinks = menu.listDrinksFor(role(ex));
        HttpUtils.sendJson(ex, 200, drinks);
    }


    public void listOrCreateDrinks(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            var drinks = menu.listDrinksFor(role(ex));
            HttpUtils.sendJson(ex, 200, drinks);
            return;
        }
        if ("POST".equalsIgnoreCase(method)) {
            // само ADMIN може да създава
//            if (role(ex) != UserRole.ADMIN) {
//                HttpUtils.sendJson(ex, 403, Map.of("error","forbidden","message","ADMIN only"));
//                return;
//            }
            try {
                var req = HttpUtils.parseJson(ex, DrinkCreateRequest.class);
                validate(req);
                DrinkResponse created = menu.createDrink(req);
                ex.getResponseHeaders().add("Location", "/menu/drinks/" + created.id());
                HttpUtils.sendJson(ex, 201, created);
            } catch (BadRequestException e) {
                HttpUtils.sendJson(ex, 400, Map.of("error","bad_request","message", e.getMessage()));
            } catch (Exception e) {
                HttpUtils.sendJson(ex, 500, Map.of("error","server_error","message","Could not create drink"));
            }
            return;
        }
        HttpUtils.methodNotAllowed(ex, "GET, POST");
    }

    public void pizzaDetailsOrUpdateOrDelete(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath();
        String[] p = path.split("/");
        if (p.length >= 5 && "details".equals(p[4])) {
            int id;
            try { id = Integer.parseInt(p[3]); } catch (NumberFormatException e) {
                HttpUtils.sendJson(ex, 400, Map.of("error","bad_request","message","invalid id")); return;
            }
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "GET"); return; }
            PizzaDetails details = menu.getPizzaDetails(id, role(ex));
            if (details == null) { HttpUtils.sendJson(ex, 404, Map.of("error","not_found")); return; }
            HttpUtils.sendJson(ex, 200, details);
            return;
        }
        HttpUtils.sendJson(ex, 404, Map.of("error","not_found"));
    }
    private void validate(PizzaCreateRequest r) {
        if (r == null) throw new BadRequestException("Empty body");
        if (r.name() == null || r.name().isBlank()) throw new BadRequestException("name is required");
        BigDecimal price = r.price();
        if (price.compareTo(BigDecimal.ZERO) < 0) throw new BadRequestException("price must be >= 0");
    }

    private void validate(DrinkCreateRequest r) {
        if (r == null) throw new BadRequestException("Empty body");
        if (r.name() == null || r.name().isBlank()) throw new BadRequestException("name is required");
        BigDecimal price = r.price();
        if (price.compareTo(BigDecimal.ZERO) < 0) throw new BadRequestException("price must be >= 0");
    }

    private void validate(PizzaUpdateRequest r) {
        if (r == null) throw new BadRequestException("Empty body");
        BigDecimal price = r.price();

        if (price != null && price.compareTo(BigDecimal.ZERO) < 0){
            throw new BadRequestException("price must be >= 0");
        }
    }

    private void validate(DrinkUpdateRequest r) {
        if (r == null) throw new BadRequestException("Empty body");
        BigDecimal price = r.price();
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0){
            throw new BadRequestException("price must be >= 0");
        }
    }
}
