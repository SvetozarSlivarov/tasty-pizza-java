package com.example.server;

import com.example.config.Beans;
import com.example.http.*;
import com.example.http.AccessLogFilter;
import com.example.http.CorsFilter;
import com.example.security.JwtAuthFilter;
import com.sun.net.httpserver.*;

public class RouteRegistrar {
    public static void registerAll(HttpServer server, Beans beans) {
        var cors = new CorsFilter();
        var access = new AccessLogFilter();
        var authRequired = new JwtAuthFilter(JwtAuthFilter.Mode.REQUIRED, beans.jwt);
        var authOptional = new JwtAuthFilter(JwtAuthFilter.Mode.OPTIONAL, beans.jwt);

        // /auth
        var authR = new ContextRouter();
        authR.register("POST", "^/register$", (ex,m) -> beans.authCtl.handleRegister(ex));
        authR.register("POST", "^/login$",    (ex,m) -> beans.authCtl.handleLogin(ex));
        HttpContext auth = server.createContext("/auth", authR::handle);
        addFilters(auth, cors, access, null);

        // /api
        var apiR = new ContextRouter();
        // pizzas
        apiR.register("GET",    "^/pizzas$",                       (ex,m) -> beans.pizzaCtl.handleList(ex));
        apiR.register("POST",   "^/pizzas$",                       (ex,m) -> beans.pizzaCtl.handleCreate(ex));
        apiR.register("GET",    "^/pizzas/(\\d+)$",                (ex,m) -> beans.pizzaCtl.handleGet(ex, Integer.parseInt(m.group(1))));
        apiR.register("PATCH",  "^/pizzas/(\\d+)$",                (ex,m) -> beans.pizzaCtl.handleUpdate(ex, Integer.parseInt(m.group(1))));
        apiR.register("DELETE", "^/pizzas/(\\d+)$",                (ex,m) -> beans.pizzaCtl.handleDelete(ex, Integer.parseInt(m.group(1))));
        apiR.register("PUT",    "^/pizzas/(\\d+)/ingredients$",    (ex,m) -> beans.pizzaCtl.handleReplaceBase(ex, Integer.parseInt(m.group(1))));
        apiR.register("PUT",    "^/pizzas/(\\d+)/allowed-ingredients$", (ex,m) -> beans.pizzaCtl.handleReplaceAllowed(ex, Integer.parseInt(m.group(1))));
        // ingredients
        apiR.register("GET",    "^/ingredients$",                  (ex,m) -> beans.ingredientCtl.handleList(ex));
        apiR.register("POST",   "^/ingredients$",                  (ex,m) -> beans.ingredientCtl.handleCreate(ex));
        apiR.register("PATCH",  "^/ingredients/(\\d+)$",           (ex,m) -> beans.ingredientCtl.handleUpdate(ex, Integer.parseInt(m.group(1))));
        apiR.register("DELETE", "^/ingredients/(\\d+)$",           (ex,m) -> beans.ingredientCtl.handleDelete(ex, Integer.parseInt(m.group(1))));
        // ingredient-types (новите)
        apiR.register("GET",    "^/ingredient-types$",             (ex,m) -> beans.ingredientTypeCtl.handleList(ex));
        apiR.register("POST",   "^/ingredient-types$",             (ex,m) -> beans.ingredientTypeCtl.handleCreate(ex));
        apiR.register("PATCH",  "^/ingredient-types/(\\d+)$",      (ex,m) -> beans.ingredientTypeCtl.handleUpdate(ex, Integer.parseInt(m.group(1))));
        apiR.register("DELETE", "^/ingredient-types/(\\d+)$",      (ex,m) -> beans.ingredientTypeCtl.handleDelete(ex, Integer.parseInt(m.group(1))));
        HttpContext api = server.createContext("/api", apiR::handle);
        addFilters(api, cors, access, authOptional);

        // /users
        var usersR = new ContextRouter();
        usersR.register("GET",    "^/me$",                   (ex,m) -> beans.userCtl.getMe(ex));
        usersR.register("PUT",    "^/me$",                   (ex,m) -> beans.userCtl.updateMe(ex));
        usersR.register("DELETE", "^/me$",                   (ex,m) -> beans.userCtl.deleteMe(ex));
        usersR.register("PUT",    "^/(\\d+)/role$",          (ex,m) -> beans.userCtl.updateRoleById(ex));
        usersR.register("DELETE", "^/(\\d+)$",               (ex,m) -> beans.userCtl.deleteById(ex));
        usersR.register("PUT",    "^/by-username/[^/]+/role$", (ex,m) -> beans.userCtl.updateRoleByUsername(ex));
        usersR.register("DELETE", "^/by-username/[^/]+$",      (ex,m) -> beans.userCtl.deleteByUsername(ex));
        HttpContext users = server.createContext("/users", usersR::handle);
        addFilters(users, cors, access, authRequired);

        // Health
        HttpContext health = server.createContext("/health", ex -> HttpUtils.sendText(ex, 200, "OK"));
        addFilters(health, cors, access, null);

        // Catch-all 404
        HttpContext notFound = server.createContext("/", ex -> HttpUtils.sendJson(ex, 404, java.util.Map.of(
                "error","not_found","path", ex.getRequestURI().getPath())));
        addFilters(notFound, cors, access, null);
    }

    private static void addFilters(HttpContext ctx, CorsFilter cors, AccessLogFilter access, JwtAuthFilter auth) {
        ctx.getFilters().add(cors);
        ctx.getFilters().add(access);
        if (auth != null) ctx.getFilters().add(auth);
    }
}
