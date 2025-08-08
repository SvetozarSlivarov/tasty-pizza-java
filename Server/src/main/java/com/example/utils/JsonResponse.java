package com.example.utils;

import com.google.gson.JsonObject;

public class JsonResponse {

    public static JsonObject success(String message) {
        JsonObject res = new JsonObject();
        res.addProperty("status", "success");
        res.addProperty("message", message);
        return res;
    }

    public static JsonObject error(String message) {
        JsonObject res = new JsonObject();
        res.addProperty("status", "error");
        res.addProperty("message", message);
        return res;
    }

    public static JsonObject unauthorized() {
        JsonObject res = new JsonObject();
        res.addProperty("status", "unauthorized");
        res.addProperty("message", "Unauthorized access");
        return res;
    }

    public static JsonObject notFound() {
        JsonObject res = new JsonObject();
        res.addProperty("status", "notFound");
        res.addProperty("message", "The page not found");
        return res;
    }

}
