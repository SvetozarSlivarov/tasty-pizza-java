package com.example.dto.ingredient;


public record IngredientView(
        Integer id,
        String name,
        IngredientTypeView type
) {}