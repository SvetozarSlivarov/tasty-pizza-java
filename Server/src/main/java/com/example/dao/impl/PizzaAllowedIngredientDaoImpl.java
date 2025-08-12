package com.example.dao.impl;

import com.example.dao.PizzaAllowedIngredientDao;
import com.example.db.DBConnection;
import com.example.model.Ingredient;
import com.example.model.Pizza;
import com.example.model.PizzaAllowedIngredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PizzaAllowedIngredientDaoImpl implements PizzaAllowedIngredientDao {

    @Override
    public List<PizzaAllowedIngredient> findByPizzaId(int pizzaId) {
        String sql = "SELECT pizza_id, ingredient_id FROM pizza_allowed_ingredients WHERE pizza_id=?";
        List<PizzaAllowedIngredient> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, pizzaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(extract(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean allow(int pizzaId, int ingredientId) {
        String sql = "INSERT INTO pizza_allowed_ingredients(pizza_id, ingredient_id) VALUES(?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, pizzaId);
            ps.setInt(2, ingredientId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean disallow(int pizzaId, int ingredientId) {
        String sql = "DELETE FROM pizza_allowed_ingredients WHERE pizza_id=? AND ingredient_id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, pizzaId);
            ps.setInt(2, ingredientId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private PizzaAllowedIngredient extract(ResultSet rs) throws SQLException {
        Pizza p = new Pizza(); p.setId(rs.getInt("pizza_id"));
        Ingredient i = new Ingredient(); i.setId(rs.getInt("ingredient_id"));
        return new PizzaAllowedIngredient(p, i);
    }
}
