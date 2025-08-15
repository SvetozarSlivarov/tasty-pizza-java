package com.example.dao.impl;

import com.example.dao.PizzaDao;
import com.example.dao.base.AbstractDao;
import com.example.model.Pizza;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PizzaDaoImpl extends AbstractDao implements PizzaDao {
    @Override
    public Pizza findById(int id) {
        String sql = "SELECT * FROM pizzas WHERE id = ?";
        try { return queryOne(sql, ps -> ps.setInt(1, id), this::map); }
        catch (SQLException e) { e.printStackTrace(); return null; }
    }

    @Override
    public List<Pizza> findAll() {
        String sql = "SELECT * FROM pizzas ORDER BY id";
        try {
            return queryList(sql, null, this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    @Override
    public List<Pizza> findAvailable() {
        String sql = "SELECT * FROM pizzas WHERE is_available = true";
        try {
            return queryList(sql, null, this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Pizza save(Pizza pizza) {
        String sql = "INSERT INTO pizzas(name, description, base_price, is_available) VALUES(?,?,?,?)";
        try {
            int id = updateReturningId(sql, ps -> {
                ps.setString(1, pizza.getName());
                ps.setString(2, pizza.getDescription());
                ps.setBigDecimal(3, pizza.getPrice());
                ps.setBoolean(4, pizza.isAvailable());
            });
            if (id <= 0) return null;
            pizza.setId(id);
            return pizza;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Pizza update(Pizza pizza) {
        String sql = "UPDATE pizzas SET name=?, description=?, base_price=?, is_available=? WHERE id=?";
        try {
            int rows = update(sql, ps -> {
                ps.setString(1, pizza.getName());
                ps.setString(2, pizza.getDescription());
                ps.setBigDecimal(3, pizza.getPrice());
                ps.setBoolean(4, pizza.isAvailable());
                ps.setInt(5, pizza.getId());
            });
            return rows > 0 ? pizza : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Pizza delete(int id) {
        try {
            Pizza existing = findById(id);
            if (existing == null) return null;
            String sql = "DELETE FROM pizzas WHERE id=?";
            int rows = update(sql, ps -> ps.setInt(1, id));
            return rows > 0 ? existing : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Pizza map(ResultSet resultSet) throws SQLException {
        Pizza pizza = new Pizza();
        pizza.setId(resultSet.getInt("id"));
        pizza.setName(resultSet.getString("name"));
        pizza.setDescription(resultSet.getString("description"));
        pizza.setPrice(resultSet.getBigDecimal("base_price"));
        pizza.setAvailable(resultSet.getBoolean("is_available"));
        return pizza;
    }
}
