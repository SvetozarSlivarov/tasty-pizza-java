package com.example.http;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

public class ContextRouter {
    @FunctionalInterface
    public interface RouteHandler { void handle(HttpExchange ex, Matcher m) throws Exception; }
    static class Route {
        final String method; final Pattern pattern; final RouteHandler handler;
        Route(String method, String regex, RouteHandler h) {
            this.method = method.toUpperCase(); this.pattern = Pattern.compile(regex); this.handler = h;
        }
    }
    private final List<Route> routes = new ArrayList<>();

    public void register(String method, String regex, RouteHandler handler) {
        routes.add(new Route(method, regex, handler));
    }

    public void handle(HttpExchange ex) throws IOException {
        String abs = ex.getRequestURI().getPath();
        String base = ex.getHttpContext().getPath();
        String rel = abs.substring(Math.min(base.length(), abs.length()));
        if (rel.isEmpty()) rel = "/";
        String method = ex.getRequestMethod().toUpperCase();

        for (Route r : routes) {
            if (!r.method.equals(method)) continue;
            Matcher m = r.pattern.matcher(rel);
            if (m.matches()) {
                try { r.handler.handle(ex, m); } catch (Exception e) { HttpUtils.send500(ex, e); }
                return;
            }
        }
        HttpUtils.sendJson(ex, 404, java.util.Map.of("error","not_found","path", rel));
    }
}