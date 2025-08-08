package com.example.network;

import com.example.controller.AuthController;
import com.google.gson.JsonObject;

public class RouteDispatcher {

    public static JsonObject dispatch(String action, JsonObject payload) {
        if (action.startsWith("auth:")) {
            return new AuthController().handle(action, payload);
        }
//        } else if (action.startsWith("pizza:")) {
//            return new PizzaController().handle(action, payload);
//        }
        JsonObject error = new JsonObject();
        error.addProperty("status", "error");
        error.addProperty("message", "Unknown action");
        return error;
    }
}
