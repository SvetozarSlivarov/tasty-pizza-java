package com.example.dao;

import com.example.model.PizzaIngredient;
import java.util.List;

public interface PizzaIngredientDao {
    List<PizzaIngredient> findByPizzaId(int pizzaId);
    List<PizzaIngredient> findByIngredientId(int ingredientId);
    boolean add(int pizzaId, int ingredientId);
    boolean remove(int pizzaId, int ingredientId);
}
