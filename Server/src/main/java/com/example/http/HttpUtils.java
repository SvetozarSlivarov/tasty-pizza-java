package com.example.http;

import com.example.exception.BadRequestException;
import com.example.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.charset.StandardCharsets;
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
    public static void methodNotAllowed(HttpExchange ex, String allow) throws IOException {
        ex.getResponseHeaders().set("Allow", allow);
        sendJson(ex, 405, Map.of("error","method_not_allowed","allow",allow));
    }
    private HttpUtils(){}
}
