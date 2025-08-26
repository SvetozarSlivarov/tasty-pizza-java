package com.example.model;

public class PizzaIngredient {
    private int pizzaId;
    private int ingredientId;
    private boolean isRemovable = true;

    public PizzaIngredient() {}

    public PizzaIngredient(int pizzaId, int ingredientId) {
        this(pizzaId, ingredientId, true);
    }

    public PizzaIngredient(int pizzaId, int ingredientId, boolean isRemovable) {
        this.pizzaId = pizzaId;
        this.ingredientId = ingredientId;
        this.isRemovable = isRemovable;
    }

    public int getPizzaId() { return pizzaId; }
    public void setPizzaId(int pizzaId) { this.pizzaId = pizzaId; }

    public int getIngredientId() { return ingredientId; }
    public void setIngredientId(int ingredientId) { this.ingredientId = ingredientId; }

    public boolean isRemovable() { return isRemovable; }
    public void setRemovable(boolean removable) { isRemovable = removable; }
}