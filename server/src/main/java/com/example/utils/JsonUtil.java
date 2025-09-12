package com.example.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;

public final class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public static ObjectMapper mapper() { return MAPPER; }

    public static <T> T fromJson(InputStream is, Class<T> type){
        try { return MAPPER.readValue(is, type); }
        catch (Exception e){ throw new RuntimeException("Invalid JSON: "+e.getMessage(), e); }
    }

    public static <T> T fromJson(String json, Class<T> type){
        try { return MAPPER.readValue(json, type); }
        catch (Exception e){ throw new RuntimeException("Invalid JSON: "+e.getMessage(), e); }
    }

    public static String toJson(Object obj){
        try { return MAPPER.writeValueAsString(obj); }
        catch (Exception e){ throw new RuntimeException("Cannot serialize", e); }
    }

    private JsonUtil(){}
}