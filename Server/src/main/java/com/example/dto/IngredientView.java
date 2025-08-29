package com.example.dto;

public record IngredientView(
        Integer id,
        String name,
        IngredientTypeView type
) {}