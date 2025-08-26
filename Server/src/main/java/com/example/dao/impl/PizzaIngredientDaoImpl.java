package com.example.dao.impl;

import com.example.dao.PizzaIngredientDao;
import com.example.dao.PizzaDao;
import com.example.dao.IngredientDao;
import com.example.dao.base.AbstractDao;
import com.example.model.PizzaIngredient;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PizzaIngredientDaoImpl extends AbstractDao implements PizzaIngredientDao {
    private final PizzaDao pizzaDao = new PizzaDaoImpl();
    private final IngredientDao ingredientDao = new IngredientDaoImpl();

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
        return add(pizzaId, ingredientId, true);
    }

    @Override
    public boolean add(int pizzaId, int ingredientId, boolean isRemovable) {
        if (pizzaDao.findById(pizzaId) == null) return false;
        if (ingredientDao.findById(ingredientId) == null) return false;

        String sql = "INSERT INTO pizza_ingredients(pizza_id, ingredient_id, is_removable) VALUES(?, ?, ?)";
        try {
            return update(sql, ps -> {
                ps.setInt(1, pizzaId);
                ps.setInt(2, ingredientId);
                ps.setBoolean(3, isRemovable);
            }) > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean updateIsRemovable(int pizzaId, int ingredientId, boolean isRemovable) {
        String sql = "UPDATE pizza_ingredients SET is_removable=? WHERE pizza_id=? AND ingredient_id=?";
        try {
            return update(sql, ps -> {
                ps.setBoolean(1, isRemovable);
                ps.setInt(2, pizzaId);
                ps.setInt(3, ingredientId);
            }) > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean remove(int pizzaId, int ingredientId) {
        String sql = "DELETE FROM pizza_ingredients WHERE pizza_id=? AND ingredient_id=?";
        try {
            return update(sql, ps -> { ps.setInt(1, pizzaId); ps.setInt(2, ingredientId); }) > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public List<PizzaIngredient> findByPizzaId(int pizzaId) {
        String sql = "SELECT pizza_id, ingredient_id, is_removable FROM pizza_ingredients WHERE pizza_id = ?";
        try {
            return queryList(sql, ps -> ps.setInt(1, pizzaId), rs ->
                    new PizzaIngredient(
                            rs.getInt("pizza_id"),
                            rs.getInt("ingredient_id"),
                            rs.getBoolean("is_removable")
                    )
            );
        } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }
}