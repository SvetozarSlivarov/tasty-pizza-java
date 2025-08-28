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
        String sql = "SELECT p.id, p.name, p.description, p.base_price, p.is_available " +
                "FROM pizzas z JOIN products p ON p.id = z.product_id WHERE z.product_id=? AND p.type='pizza'";
        try {
            return queryOne(sql, ps -> ps.setInt(1, id), this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Pizza> findAll() {
        String sql = "SELECT p.id, p.name, p.description, p.base_price, p.is_available " +
                "FROM pizzas z JOIN products p ON p.id = z.product_id WHERE p.type='pizza'";
        try {
            return queryList(sql, null, this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Pizza> findAvailable() {
        String sql = "SELECT p.id, p.name, p.description, p.base_price, p.is_available " +
                "FROM pizzas z JOIN products p ON p.id = z.product_id " +
                "WHERE p.type='pizza' AND p.is_available=TRUE";
        try {
            return queryList(sql, null, this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Pizza save(Pizza pizza) {
        try {
            // 1) insert into products
            String insProduct = "INSERT INTO products(type, name, description, base_price, is_available) " +
                    "VALUES ('pizza', ?, ?, ?, ?)";
            int id = updateReturningId(insProduct, ps -> {
                ps.setString(1, pizza.getName());
                ps.setString(2, pizza.getDescription());
                ps.setBigDecimal(3, pizza.getPrice());
                ps.setBoolean(4, pizza.isAvailable());
            });
            if (id <= 0) return null;

            // 2) marker row in pizzas
            String insPizza = "INSERT INTO pizzas(product_id) VALUES (?)";
            update(insPizza, ps -> ps.setInt(1, id));

            pizza.setId(id);
            return pizza;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Pizza update(Pizza pizza) {
        String sql = "UPDATE products SET name=?, description=?, base_price=?, is_available=? WHERE id=? AND type='pizza'";
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
        Pizza existing = findById(id);
        if (existing == null) return null;
        try {
            int rows = update("DELETE FROM products WHERE id=? AND type='pizza'", ps -> ps.setInt(1, id));
            return rows > 0 ? existing : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Pizza map(ResultSet rs) throws SQLException {
        Pizza pizza = new Pizza();
        pizza.setId(rs.getInt("id"));
        pizza.setName(rs.getString("name"));
        pizza.setDescription(rs.getString("description"));
        pizza.setPrice(rs.getBigDecimal("base_price"));
        pizza.setAvailable(rs.getBoolean("is_available"));
        return pizza;
    }
}
