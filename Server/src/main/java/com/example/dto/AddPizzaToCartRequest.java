package com.example.dto;

import java.util.List;

public record AddPizzaToCartRequest(
        int productId,
        Integer variantId,
        int quantity,
        String note,
        List<Integer> removeIngredientIds,
        List<Integer> addIngredientIds
) {}
