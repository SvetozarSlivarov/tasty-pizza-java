package com.example.dao.impl;

import com.example.model.Ingredient;
import com.example.model.Pizza;
import java.util.List;

public class PizzaDetails {
    private Pizza pizza;
    private List<Ingredient> ingredients;
    private List<Ingredient> allowedIngredients;

    public PizzaDetails(Pizza pizza, List<Ingredient> ingredients, List<Ingredient> allowedIngredients) {
        this.pizza = pizza;
        this.ingredients = ingredients;
        this.allowedIngredients = allowedIngredients;
    }

    public Pizza getPizza() { return pizza; }
    public List<Ingredient> getIngredients() { return ingredients; }
    public List<Ingredient> getAllowedIngredients() { return allowedIngredients; }
}
