// src/com/example/controller/MenuController.java
package com.example.controller;

import com.example.http.HttpUtils;
import com.example.model.Drink;
import com.example.model.Pizza;
import com.example.model.dto.PizzaDetails;
import com.example.model.enums.UserRole;
import com.example.service.MenuService;
import com.example.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MenuController {
    private final MenuService menu;

    public MenuController(MenuService menu){ this.menu = menu; }

    private UserRole role(HttpExchange ex) {
        // ако има логнат user -> можеш да вържеш реална роля; за сега приемаме CUSTOMER
        return UserRole.CUSTOMER;
    }

    // GET /menu/pizzas
    public void listPizzas(HttpExchange ex) throws IOException {
        if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) { HttpUtils.methodNotAllowed(ex, "GET"); return; }
        List<Pizza> pizzas = menu.listPizzasFor(role(ex));
        HttpUtils.sendJson(ex, 200, pizzas);
    }

    // GET /menu/drinks  | POST /menu/drinks (примерно създаване в DAO ако имаш)
    public void listOrCreateDrinks(HttpExchange ex) throws IOException {
        String method = ex.getRequestMethod();
        if ("GET".equalsIgnoreCase(method)) {
            List<Drink> drinks = menu.listDrinksFor(role(ex));
            HttpUtils.sendJson(ex, 200, drinks);
            return;
        }
        if ("POST".equalsIgnoreCase(method)) {
            // тук, ако имаш DrinkDao.create(...) в MenuService, можеш да добавиш десериализация и създаване
            HttpUtils.sendJson(ex, 501, Map.of("error","not_implemented","message","Add create drink in MenuService/DAO"));
            return;
        }
        HttpUtils.methodNotAllowed(ex, "GET, POST");
    }

    // /menu/pizzas/{id}/details  (GET; по желание PUT/DELETE нататък)
    public void pizzaDetailsOrUpdateOrDelete(HttpExchange ex) throws IOException {
        String path = ex.getRequestURI().getPath(); // /menu/pizzas/{id}/details
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
}
