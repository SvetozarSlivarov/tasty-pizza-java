package com.example.dto;

import java.util.List;

public record UpdateCartItemRequest(
        Integer quantity,
        Integer variantId,
        String note,
        List<Integer> removeIngredientIds,
        List<Integer> addIngredientIds
) {}
