package com.example.dao;

import java.util.List;

public interface PizzaAllowedIngredientDao {
    List<Integer> findIngredientIdsByPizzaId(int pizzaId);
    boolean allow(int pizzaId, int ingredientId);
    boolean disallow(int pizzaId, int ingredientId);
}
