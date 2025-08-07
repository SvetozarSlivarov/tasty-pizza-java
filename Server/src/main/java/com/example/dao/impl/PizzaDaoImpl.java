package com.example.dao.impl;

import com.example.dao.PizzaDao;
import com.example.db.DBConnection;
import com.example.model.Pizza;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PizzaDaoImpl implements PizzaDao {

    @Override
    public Pizza findById(int id) {
        String sql = "SELECT * FROM pizzas WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractPizza(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Pizza> findAll() {
        List<Pizza> pizzas = new ArrayList<>();
        String sql = "SELECT * FROM pizzas";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pizzas.add(extractPizza(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pizzas;
    }

    @Override
    public boolean save(Pizza pizza) {
        String sql = "INSERT INTO pizzas (name, description, price, is_available) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pizza.getName());
            ps.setString(2, pizza.getDescription());
            ps.setBigDecimal(3, pizza.getPrice());
            ps.setBoolean(4, pizza.isAvailable());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean update(Pizza pizza) {
        String sql = "UPDATE pizzas SET name = ?, description = ?, price = ?, is_available = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pizza.getName());
            ps.setString(2, pizza.getDescription());
            ps.setBigDecimal(3, pizza.getPrice());
            ps.setBoolean(4, pizza.isAvailable());
            ps.setInt(5, pizza.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM pizzas WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Pizza extractPizza(ResultSet rs) throws SQLException {
        return new Pizza(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getBoolean("is_available")
        );
    }
}
