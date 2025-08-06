package com.example.model;

public class PizzaIngredient {
    private Pizza pizza;
    private Ingredient ingredient;

    public PizzaIngredient() {}

    public PizzaIngredient(Pizza pizza, Ingredient ingredient) {
        this.pizza = pizza;
        this.ingredient = ingredient;
    }

    public Pizza getPizza() {
        return pizza;
    }

    public void setPizza(Pizza pizza) {
        this.pizza = pizza;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    @Override
    public String toString() {
        return "PizzaIngredient{" +
                "pizza=" + (pizza != null ? pizza.getName() : "null") +
                ", ingredient=" + (ingredient != null ? ingredient.getName() : "null") +
                '}';
    }
}
