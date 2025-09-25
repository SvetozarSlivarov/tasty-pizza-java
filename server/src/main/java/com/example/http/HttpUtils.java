package com.example.http;

import com.example.exception.BadRequestException;
import com.example.model.enums.UserRole;
import com.example.security.JwtService;
import com.example.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class HttpUtils {
    public static String readBody(HttpExchange ex) throws IOException {
        try (InputStream is = ex.getRequestBody(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            is.transferTo(baos);
            return baos.toString(StandardCharsets.UTF_8);
        }
    }
    public static <T> T parseJson(HttpExchange ex, Class<T> clazz) {
        try (InputStream is = ex.getRequestBody()) {
            if (is == null) throw new BadRequestException("Empty body");
            return JsonUtil.fromJson(is, clazz);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid JSON: " + e.getOriginalMessage());
        } catch (IOException e) {
            throw new RuntimeException("I/O reading request body", e);
        }
    }
    public static void sendText(HttpExchange ex, int status, String text) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type","text/plain; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); } finally { ex.close(); }
    }
    public static void sendJson(HttpExchange ex, int status, Object obj) throws IOException {
        byte[] bytes = JsonUtil.toJson(obj).getBytes(StandardCharsets.UTF_8);
        Headers h = ex.getResponseHeaders();
        h.set("Content-Type","application/json; charset=utf-8");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(bytes); } finally { ex.close(); }
    }
    public static void sendStatus(HttpExchange ex, int statusCode) throws IOException {
        ex.sendResponseHeaders(statusCode, -1);
        ex.close();
    }
    public static void methodNotAllowed(HttpExchange ex, String allow) throws IOException {
        ex.getResponseHeaders().set("Allow", allow);
        sendJson(ex, 405, Map.of("error","method_not_allowed","allow",allow));
    }
    public static void requireMethod(HttpExchange ex, String expected) throws IOException {
        String actual = ex.getRequestMethod();
        if (!expected.equalsIgnoreCase(actual)) {
            sendJson(ex, 405, Map.of("error", "method_not_allowed", "allowed", expected));
            throw new IllegalStateException("Invalid method: " + actual);
        }
    }
    public static void requireRole(HttpExchange ex, JwtService jwt, UserRole required) throws IOException {
        UserRole role = roleOr(ex, UserRole.CUSTOMER, jwt);
        if (role.ordinal() < required.ordinal()) {
            sendJson(ex, 403, Map.of("error", "forbidden"));
            throw new IllegalStateException("Forbidden: need role " + required);
        }
    }
    public static UserRole roleOr(HttpExchange ex, UserRole fallback, JwtService jwt) {
        try {
            String auth = ex.getRequestHeaders().getFirst("Authorization");
            if (auth == null || !auth.startsWith("Bearer ")) return fallback;
            String token = auth.substring(7);
            return jwt.extractUserRole(token);
        } catch (Exception e) {
            return fallback;
        }
    }
    public static Integer tryGetUserId(HttpExchange ex, JwtService jwt) {
        String auth = ex.getRequestHeaders().getFirst("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            return null;
        }
        String token = auth.substring("Bearer ".length()).trim();
        try {
            return jwt.verifyAndGetUserId(token);
        } catch (Exception e) {
            return null;
        }
    }
    public static void setCookie(HttpExchange ex, String name, String value, int maxAgeSeconds) {
        setCookie(ex, name, value, maxAgeSeconds, false, "Lax");
    }
    public static Integer tryGetCookieInt(HttpExchange ex, String name) {
        List<String> cookies = ex.getRequestHeaders().get("Cookie");
        if (cookies == null) return null;

        for (String header : cookies) {
            String[] parts = header.split(";");
            for (String part : parts) {
                String[] kv = part.trim().split("=", 2);
                if (kv.length != 2) continue;

                String k = kv[0].trim();
                if (!k.equals(name)) continue;

                String v = kv[1].trim();
                if (v.length() >= 2 && v.charAt(0) == '"' && v.charAt(v.length()-1) == '"') {
                    v = v.substring(1, v.length()-1);
                }
                try {
                    return Integer.parseInt(v);
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }

    public static String tryGetCookie(HttpExchange ex, String name) {
        List<String> cookies = ex.getRequestHeaders().get("Cookie");
        if (cookies == null) return null;

        for (String header : cookies) {
            String[] parts = header.split(";");
            for (String part : parts) {
                String[] kv = part.trim().split("=", 2);
                if (kv.length != 2) continue;

                String k = kv[0].trim();
                if (!k.equals(name)) continue;

                String v = kv[1].trim();
                if (v.length() >= 2 && v.charAt(0) == '"' && v.charAt(v.length()-1) == '"') {
                    v = v.substring(1, v.length()-1);
                }
                return v;
            }
        }
        return null;
    }

    public static void setCookie(HttpExchange ex, String name, String value, int maxAgeSeconds,
                                 boolean secure, String sameSite) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(value)
                .append("; Path=/")
                .append("; HttpOnly")
                .append("; Max-Age=").append(maxAgeSeconds);
        if (sameSite != null) sb.append("; SameSite=").append(sameSite);
        if (secure || "None".equalsIgnoreCase(sameSite)) sb.append("; Secure");
        ex.getResponseHeaders().add("Set-Cookie", sb.toString());
    }
    public static void send500(HttpExchange ex, Exception e) throws IOException {
        String message = e.getMessage() != null ? e.getMessage() : e.toString();
        sendJson(ex, 500, Map.of(
                "error", "internal_server_error",
                "message", message
        ));
    }
    public static String queryParam(HttpExchange ex, String name) {
        return parseQuery(ex).get(name);
    }
    public static Integer queryParamInt(HttpExchange ex, String name) {
        String v = queryParam(ex, name);
        if (v == null || v.isBlank()) return null;
        try { return Integer.parseInt(v); }
        catch (NumberFormatException e) { return null; }
    }
    public static UserRole resolveRole(HttpExchange ex, JwtService jwt) {
        Object attr = ex.getAttribute("userRole");
        if (attr instanceof UserRole ur) return ur;

        String token = extractBearerToken(ex);
        if (token != null) {
            try {
                return jwt.extractUserRole(token);
            } catch (Exception ignored) {}
        }
        return UserRole.CUSTOMER;
    }
    public static String extractBearerToken(HttpExchange ex) {
        String auth = ex.getRequestHeaders().getFirst("Authorization");
        if (auth == null) return null;
        auth = auth.trim();
        if (auth.regionMatches(true, 0, "Bearer ", 0, 7) && auth.length() > 7) {
            return auth.substring(7).trim();
        }
        return null;
    }
    public static Map<String, String> parseQuery(HttpExchange ex) {
        Map<String, String> result = new HashMap<>();
        String query = ex.getRequestURI().getRawQuery();
        if (query == null || query.isEmpty()) return result;

        for (String pair : query.split("&")) {
            int idx = pair.indexOf('=');
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                result.put(key, value);
            } else {
                String key = URLDecoder.decode(pair, StandardCharsets.UTF_8);
                result.put(key, "");
            }
        }
        return result;
    }
    private HttpUtils(){}
}
