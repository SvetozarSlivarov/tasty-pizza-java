package com.example.dao;

import java.util.List;
import com.example.model.PizzaIngredient;

public interface PizzaIngredientDao {
    List<Integer> findIngredientIdsByPizzaId(int pizzaId);
    List<Integer> findPizzaIdsByIngredientId(int ingredientId);

    boolean add(int pizzaId, int ingredientId);

    boolean add(int pizzaId, int ingredientId, boolean isRemovable);

    boolean updateIsRemovable(int pizzaId, int ingredientId, boolean isRemovable);

    boolean remove(int pizzaId, int ingredientId);

    List<PizzaIngredient> findByPizzaId(int pizzaId);
}