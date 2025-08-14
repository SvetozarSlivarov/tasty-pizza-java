package com.example.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    public static <T> T fromJson(String json, Class<T> type){
        try { return MAPPER.readValue(json, type); } catch (Exception e){ throw new RuntimeException("Invalid JSON: "+e.getMessage(), e); }
    }
    public static String toJson(Object obj){
        try { return MAPPER.writeValueAsString(obj); } catch (Exception e){ throw new RuntimeException("Cannot serialize", e); }
    }
    private JsonUtil(){}
}
