package com.example.http;
import com.sun.net.httpserver.*;
import java.io.IOException;

public class CorsFilter extends Filter {
    @Override public void doFilter(HttpExchange ex, Chain chain) throws IOException {
        var h = ex.getResponseHeaders();
        h.set("Access-Control-Allow-Origin", "*");
        h.set("Access-Control-Allow-Headers", "Content-Type, Authorization");
        h.set("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        h.set("Access-Control-Max-Age", "86400");
        if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) { ex.sendResponseHeaders(204, -1); ex.close(); return; }
        chain.doFilter(ex);
    }
    @Override public String description() { return "CORS"; }
}
