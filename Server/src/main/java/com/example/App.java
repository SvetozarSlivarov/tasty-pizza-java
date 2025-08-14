// src/com/example/App.java
package com.example;

import com.example.controller.AuthController;
import com.example.controller.MenuController;
import com.example.http.AccessLogFilter;
import com.example.http.CorsFilter;
import com.example.http.HttpUtils;
import com.example.security.AuthFilter;
import com.example.security.JwtAuthFilter;
import com.example.security.JwtService;
import com.example.security.TokenService;
import com.example.service.MenuService;
import com.example.service.UserService;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) throws Exception {
        int port = Integer.getInteger("PORT", 8080);
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);

        var userService = new UserService();
        var menuService = new MenuService();
        //var tokenService = new TokenService();
        String secret = System.getenv().getOrDefault("JWT_SECRET",
                "rZg9l5mVxqkz6j+QG3WkX1XzF9yR8m2cQ3ZrT5wY2pA="); // ПРИМЕР! Смени го.
        long ttlSeconds = 3600; // 1 час

        var jwt = new JwtService(secret, ttlSeconds);

        var authController = new AuthController(userService, jwt);
        var menuController = new MenuController(menuService);

        var cors = new CorsFilter();
        var access = new AccessLogFilter();
        var authRequired = new JwtAuthFilter(JwtAuthFilter.Mode.REQUIRED, jwt);
        var authOptional = new JwtAuthFilter(JwtAuthFilter.Mode.OPTIONAL, jwt);

        // Auth
        HttpContext register = server.createContext("/auth/register", authController::handleRegister);
        register.getFilters().add(cors); register.getFilters().add(access);

        HttpContext login = server.createContext("/auth/login", authController::handleLogin);
        login.getFilters().add(cors); login.getFilters().add(access);

        HttpContext drinks = server.createContext("/menu/drinks", menuController::listOrCreateDrinks);
        drinks.getFilters().add(cors); drinks.getFilters().add(authOptional); drinks.getFilters().add(access);

        HttpContext pizzas = server.createContext("/menu/pizzas", menuController::listPizzas);
        pizzas.getFilters().add(cors); pizzas.getFilters().add(authOptional); pizzas.getFilters().add(access);

        HttpContext pizzaDetails = server.createContext("/menu/pizzas/");
        pizzaDetails.setHandler(exchange -> menuController.pizzaDetailsOrUpdateOrDelete(exchange));
        pizzaDetails.getFilters().add(cors); pizzaDetails.getFilters().add(authOptional); pizzaDetails.getFilters().add(access);

        // Health
        HttpContext health = server.createContext("/health", ex -> HttpUtils.sendText(ex, 200, "OK"));
        health.getFilters().add(cors); health.getFilters().add(access);

        // 404 catch-all
        var notFound = server.createContext("/", ex -> HttpUtils.sendJson(ex, 404, java.util.Map.of(
                "error","not_found","path", ex.getRequestURI().getPath())));
        notFound.getFilters().add(cors); notFound.getFilters().add(access);

        server.setExecutor(Executors.newFixedThreadPool(Math.max(4, Runtime.getRuntime().availableProcessors()*2)));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> { System.out.println("\nShutting down..."); server.stop(1); }));

        System.out.println("HTTP server listening on http://localhost:" + port);
        server.start();
    }
}
