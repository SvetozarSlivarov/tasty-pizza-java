package com.example.dto;

public record PizzaIngredientView(
        int ingredientId,
        String name,
        IngredientTypeView type,
        boolean isRemovable
) {}
