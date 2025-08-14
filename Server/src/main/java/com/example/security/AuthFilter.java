package com.example.security;

import com.example.http.HttpUtils;
import com.sun.net.httpserver.*;
import java.io.IOException;

public class AuthFilter extends Filter {
    public enum Mode { REQUIRED, OPTIONAL }
    private final Mode mode;
    private final TokenService tokens;

    public AuthFilter(Mode mode, TokenService tokens){ this.mode=mode; this.tokens=tokens; }

    @Override public void doFilter(HttpExchange ex, Chain chain) throws IOException {
        String auth = ex.getRequestHeaders().getFirst("Authorization");
        String user = null;
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring("Bearer ".length()).trim();
            user = tokens.resolveUser(token);
        }
        if (mode == Mode.REQUIRED && user == null) {
            ex.getResponseHeaders().add("WWW-Authenticate","Bearer realm=\"api\"");
            HttpUtils.sendJson(ex, 401, java.util.Map.of("error","unauthorized")); return;
        }
        if (user != null) ex.setAttribute("user", user);
        chain.doFilter(ex);
    }
    @Override public String description(){ return "auth"; }
}
