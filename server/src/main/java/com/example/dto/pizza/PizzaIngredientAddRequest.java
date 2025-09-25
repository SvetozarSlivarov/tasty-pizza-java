package com.example.dto.pizza;

public record PizzaIngredientAddRequest(
        int ingredientId,
        boolean isRemovable
) {}
