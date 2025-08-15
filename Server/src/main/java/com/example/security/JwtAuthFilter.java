package com.example.security;

import com.example.http.HttpUtils;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

public class JwtAuthFilter extends Filter {
    public enum Mode { REQUIRED, OPTIONAL }
    private final Mode mode;
    private final JwtService jwt;

    public JwtAuthFilter(Mode mode, JwtService jwt) { this.mode = mode; this.jwt = jwt; }

    @Override public void doFilter(HttpExchange ex, Chain chain) throws IOException {
        String auth = ex.getRequestHeaders().getFirst("Authorization");
        boolean hasAuth = auth != null && auth.startsWith("Bearer ");
        if (hasAuth) {
            String token = auth.substring("Bearer ".length()).trim();
            try {
                var u = jwt.verify(token);
                ex.setAttribute("user", u.username);
                if (u.role != null) {
                    ex.setAttribute("role", u.role);
                }
            } catch (Exception e) {
                if (mode == Mode.REQUIRED) {
                    ex.getResponseHeaders().add("WWW-Authenticate", "Bearer error=\"invalid_token\"");
                    HttpUtils.sendJson(ex, 401, Map.of("error","invalid_token")); return;
                }
            }
        } else if (mode == Mode.REQUIRED) {
            ex.getResponseHeaders().add("WWW-Authenticate", "Bearer realm=\"api\"");
            HttpUtils.sendJson(ex, 401, Map.of("error","missing_token")); return;
        }
        chain.doFilter(ex);
    }

    @Override public String description() { return "JWT auth"; }
}
