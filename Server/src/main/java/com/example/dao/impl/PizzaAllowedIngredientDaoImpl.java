package com.example.dao.impl;

import com.example.dao.PizzaAllowedIngredientDao;
import com.example.dao.base.AbstractDao;
import com.example.model.PizzaAllowedIngredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PizzaAllowedIngredientDaoImpl extends AbstractDao implements PizzaAllowedIngredientDao {
    @Override
    public List<PizzaAllowedIngredient> findByPizzaId(int pizzaId) {
        String sql = "SELECT pizza_id, ingredient_id FROM pizza_allowed_ingredients WHERE pizza_id=?";
        try { return queryList(sql, ps -> ps.setInt(1, pizzaId), this::map); }
        catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    @Override
    public boolean allow(int pizzaId, int ingredientId) {
        String sql = "INSERT INTO pizza_allowed_ingredients(pizza_id, ingredient_id) VALUES(?,?)";
        try { return update(sql, ps -> { ps.setInt(1, pizzaId); ps.setInt(2, ingredientId); }) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean disallow(int pizzaId, int ingredientId) {
        String sql = "DELETE FROM pizza_allowed_ingredients WHERE pizza_id=? AND ingredient_id=?";
        try { return update(sql, ps -> { ps.setInt(1, pizzaId); ps.setInt(2, ingredientId); }) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private PizzaAllowedIngredient map(ResultSet rs) throws SQLException {
        var p = new com.example.model.Pizza(); p.setId(rs.getInt("pizza_id"));
        var i = new com.example.model.Ingredient(); i.setId(rs.getInt("ingredient_id"));
        return new com.example.model.PizzaAllowedIngredient(p, i);
    }
}
