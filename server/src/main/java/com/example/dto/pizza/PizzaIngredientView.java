package com.example.dto.pizza;

import com.example.dto.ingredient.IngredientTypeView;

public record PizzaIngredientView(
        int ingredientId,
        String name,
        IngredientTypeView type,
        boolean isRemovable
) {}
