package com.example.dto.ingredient;

import com.example.dto.ingredient.IngredientTypeView;

public record IngredientView(
        Integer id,
        String name,
        IngredientTypeView type
) {}