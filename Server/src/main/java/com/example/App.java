// src/com/example/App.java
package com.example;

import com.example.controller.*;
import com.example.http.AccessLogFilter;
import com.example.http.CorsFilter;
import com.example.http.HttpUtils;
import com.example.security.JwtAuthFilter;
import com.example.security.JwtService;
import com.example.service.IngredientService;
import com.example.service.MenuService;
import com.example.service.PizzaService;
import com.example.service.UserService;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import com.example.db.DatabaseInitializer;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class App {
    public static void main(String[] args) throws Exception {

        try {
            DatabaseInitializer.initialize();
        } catch (Throwable t) {
            System.err.println("FATAL: Database initialization failed.");
            t.printStackTrace();
            System.exit(1); // не стартирай сървъра без БД
        }


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
        UserController userController = new UserController(userService);
        var pizzaService = new PizzaService();
        var ingredientService = new IngredientService();

        var pizzaController = new PizzaController(pizzaService, jwt);
        var ingredientController = new IngredientController(ingredientService, jwt);

        var cors = new CorsFilter();
        var access = new AccessLogFilter();
        var authRequired = new JwtAuthFilter(JwtAuthFilter.Mode.REQUIRED, jwt);
        var authOptional = new JwtAuthFilter(JwtAuthFilter.Mode.OPTIONAL, jwt);

        // Auth
        HttpContext register = server.createContext("/auth/register", authController::handleRegister);
        HttpContext login = server.createContext("/auth/login", authController::handleLogin);
        HttpContext pizzas = server.createContext("/api/pizzas", ex -> {
            try {
                String method = ex.getRequestMethod();
                var path = ex.getRequestURI().getPath();
                // /api/pizzas or /api/pizzas/{id} or /api/pizzas/{id}/ingredients ...
                var parts = path.split("/");
                if (parts.length == 3) {
                    if ("GET".equals(method)) pizzaController.handleList(ex);
                    else if ("POST".equals(method)) pizzaController.handleCreate(ex);
                    else HttpUtils.methodNotAllowed(ex, "GET,POST");
                } else if (parts.length >= 4) {
                    int id = Integer.parseInt(parts[3]);
                    if (parts.length == 4) {
                        if ("GET".equals(method)) pizzaController.handleGet(ex, id);
                        else if ("PATCH".equals(method)) pizzaController.handleUpdate(ex, id);
                        else if ("DELETE".equals(method)) pizzaController.handleDelete(ex, id);
                        else HttpUtils.methodNotAllowed(ex, "GET,PATCH,DELETE");
                    } else if (parts.length == 5) {
                        String sub = parts[4];
                        if ("ingredients".equals(sub) && "PUT".equals(method))
                            pizzaController.handleReplaceBase(ex, id);
                        else if ("allowed-ingredients".equals(sub) && "PUT".equals(method))
                            pizzaController.handleReplaceAllowed(ex, id);
                        else HttpUtils.methodNotAllowed(ex, "PUT");
                    } else HttpUtils.notFound(ex);
                } else HttpUtils.notFound(ex);
            } catch (Exception e) { HttpUtils.send500(ex, e); }
        });

        HttpContext ingredients = server.createContext("/api/ingredients", ex -> {
            try {
                String method = ex.getRequestMethod();
                var path = ex.getRequestURI().getPath();
                var parts = path.split("/");
                if (parts.length == 3) {
                    if ("GET".equals(method)) ingredientController.handleList(ex);
                    else if ("POST".equals(method)) ingredientController.handleCreate(ex);
                    else HttpUtils.methodNotAllowed(ex, "GET,POST");
                } else if (parts.length == 4) {
                    int id = Integer.parseInt(parts[3]);
                    if ("PATCH".equals(method)) ingredientController.handleUpdate(ex, id);
                    else if ("DELETE".equals(method)) ingredientController.handleDelete(ex, id);
                    else HttpUtils.methodNotAllowed(ex, "PATCH,DELETE");
                } else HttpUtils.notFound(ex);
            } catch (Exception e) { HttpUtils.send500(ex, e); }
        });

        HttpContext me = server.createContext("/users/me", ex -> {
            String m = ex.getRequestMethod();
            switch (m) {
                case "GET"    -> userController.getMe(ex);
                case "PUT"    -> userController.updateMe(ex);
                case "DELETE" -> userController.deleteMe(ex);
                default       -> HttpUtils.methodNotAllowed(ex, "GET, PUT, DELETE");
            }
        });

        HttpContext byId = server.createContext("/users/", ex -> {
            String path = ex.getRequestURI().getPath();
            String method = ex.getRequestMethod();

            if (path.matches("^/users/\\d+/role$") && "PUT".equalsIgnoreCase(method)) {
                userController.updateRoleById(ex);
            } else if (path.matches("^/users/\\d+$") && "DELETE".equalsIgnoreCase(method)) {
                userController.deleteById(ex);
            } else {
                HttpUtils.sendJson(ex, 404, java.util.Map.of("error", "not_found"));
            }
        });

        HttpContext byUsername = server.createContext("/users/by-username/", ex -> {
            String path = ex.getRequestURI().getPath();
            String method = ex.getRequestMethod();

            if (path.matches("^/users/by-username/[^/]+/role$") && "PUT".equalsIgnoreCase(method)) {
                userController.updateRoleByUsername(ex);
            } else if (path.matches("^/users/by-username/[^/]+$") && "DELETE".equalsIgnoreCase(method)) {
                userController.deleteByUsername(ex);
            } else {
                HttpUtils.sendJson(ex, 404, java.util.Map.of("error", "not_found"));
            }
        });
        addFilters(pizzas, cors, access, authOptional);
        addFilters(ingredients, cors, access, authOptional);
        addFilters(me, cors, access, authRequired);
        addFilters(byId, cors, access, authRequired);
        addFilters(byUsername, cors, access, authRequired);
        addFilters(register, cors, access, null);
        addFilters(login, cors, access, null);

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
    private static void addFilters(HttpContext ctx,
                                   CorsFilter cors,
                                   AccessLogFilter access,
                                   JwtAuthFilter auth) {
        ctx.getFilters().add(cors);
        ctx.getFilters().add(access);
        if (auth != null) ctx.getFilters().add(auth);
    }
}
