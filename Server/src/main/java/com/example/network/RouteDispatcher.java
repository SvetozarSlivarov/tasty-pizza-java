package com.example.network;

import com.example.controller.AuthController;
import com.example.controller.MenuController;
// com.example.controller.OrderController;
import com.example.utils.JsonResponse;
import com.example.utils.JsonUtil;
import com.fasterxml.jackson.databind.JsonNode;

public class RouteDispatcher {

    public static JsonNode dispatch(String action, JsonNode payload) {
        if (action.startsWith("auth:")) {
            return new AuthController().handle(action, payload);
        }
        else if (action.startsWith("menu:")) {
            return new MenuController().handle(action, payload);
        }
//        else if (action.startsWith("order:")) {
//            return new OrderController().handle(action, payload);
//        }

        return JsonResponse.error("Unknown action: " + action);
    }
}
