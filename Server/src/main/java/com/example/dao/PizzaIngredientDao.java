package com.example.dao;

import java.util.List;

public interface PizzaIngredientDao {
    List<Integer> findIngredientIdsByPizzaId(int pizzaId);
    List<Integer> findPizzaIdsByIngredientId(int ingredientId);
    boolean add(int pizzaId, int ingredientId);
    boolean remove(int pizzaId, int ingredientId);
}