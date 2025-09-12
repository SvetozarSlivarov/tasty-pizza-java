package com.example.dto;

public record PizzaIngredientAddRequest(
        int ingredientId,
        boolean isRemovable
) {}
