package com.example.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonResponse {
    public static JsonNode success(String message) {
        ObjectNode json = JsonUtil.mapper.createObjectNode();
        json.put("status", "success");
        json.put("message", message);
        return json;
    }

    public static JsonNode success(String message, JsonNode data) {
        ObjectNode json = (ObjectNode) success(message);
        json.set("data", data);
        return json;
    }

    public static JsonNode error(String message) {
        ObjectNode json = JsonUtil.mapper.createObjectNode();
        json.put("status", "error");
        json.put("message", message);
        return json;
    }
}
