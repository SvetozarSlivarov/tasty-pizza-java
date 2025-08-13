package com.example.dao.impl;

import com.example.dao.PizzaIngredientDao;
import com.example.dao.base.AbstractDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PizzaIngredientDaoImpl extends AbstractDao implements PizzaIngredientDao {

    @Override
    public List<Integer> findIngredientIdsByPizzaId(int pizzaId) {
        String sql = "SELECT ingredient_id FROM pizza_ingredients WHERE pizza_id = ?";
        try {
            return queryList(sql, ps -> ps.setInt(1, pizzaId), rs -> rs.getInt("ingredient_id"));
        } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    @Override
    public List<Integer> findPizzaIdsByIngredientId(int ingredientId) {
        String sql = "SELECT pizza_id FROM pizza_ingredients WHERE ingredient_id = ?";
        try {
            return queryList(sql, ps -> ps.setInt(1, ingredientId), rs -> rs.getInt("pizza_id"));
        } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    @Override
    public boolean add(int pizzaId, int ingredientId) {
        String sql = "INSERT INTO pizza_ingredients(pizza_id, ingredient_id) VALUES(?, ?)";
        try {
            return update(sql, ps -> { ps.setInt(1, pizzaId); ps.setInt(2, ingredientId); }) > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean remove(int pizzaId, int ingredientId) {
        String sql = "DELETE FROM pizza_ingredients WHERE pizza_id=? AND ingredient_id=?";
        try {
            return update(sql, ps -> { ps.setInt(1, pizzaId); ps.setInt(2, ingredientId); }) > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}
