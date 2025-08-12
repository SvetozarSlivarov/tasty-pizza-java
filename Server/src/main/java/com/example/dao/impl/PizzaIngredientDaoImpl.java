package com.example.dao.impl;

import com.example.dao.PizzaIngredientDao;
import com.example.dao.base.AbstractDao;
import com.example.model.PizzaIngredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PizzaIngredientDaoImpl extends AbstractDao implements PizzaIngredientDao {
    @Override
    public List<PizzaIngredient> findByPizzaId(int pizzaId) {
        String sql = "SELECT pizza_id, ingredient_id FROM pizza_ingredients WHERE pizza_id=?";
        try { return queryList(sql, ps -> ps.setInt(1, pizzaId), this::map); }
        catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    @Override
    public List<PizzaIngredient> findByIngredientId(int ingredientId) {
        String sql = "SELECT pizza_id, ingredient_id FROM pizza_ingredients WHERE ingredient_id=?";
        try { return queryList(sql, ps -> ps.setInt(1, ingredientId), this::map); }
        catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    @Override
    public boolean add(int pizzaId, int ingredientId) {
        String sql = "INSERT INTO pizza_ingredients(pizza_id, ingredient_id) VALUES(?,?)";
        try { return update(sql, ps -> { ps.setInt(1, pizzaId); ps.setInt(2, ingredientId); }) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean remove(int pizzaId, int ingredientId) {
        String sql = "DELETE FROM pizza_ingredients WHERE pizza_id=? AND ingredient_id=?";
        try { return update(sql, ps -> { ps.setInt(1, pizzaId); ps.setInt(2, ingredientId); }) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private PizzaIngredient map(ResultSet rs) throws SQLException {
        var p = new com.example.model.Pizza(); p.setId(rs.getInt("pizza_id"));
        var i = new com.example.model.Ingredient(); i.setId(rs.getInt("ingredient_id"));
        return new com.example.model.PizzaIngredient(p, i);
    }
}
