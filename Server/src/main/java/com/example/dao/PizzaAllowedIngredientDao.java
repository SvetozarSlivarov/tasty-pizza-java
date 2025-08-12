package com.example.dao;

import com.example.model.PizzaAllowedIngredient;
import java.util.List;

public interface PizzaAllowedIngredientDao {
    List<PizzaAllowedIngredient> findByPizzaId(int pizzaId);
    boolean allow(int pizzaId, int ingredientId);
    boolean disallow(int pizzaId, int ingredientId);
}
