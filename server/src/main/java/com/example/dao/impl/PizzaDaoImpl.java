package com.example.dao.impl;

import com.example.dao.PizzaDao;
import com.example.dao.base.AbstractDao;
import com.example.model.Pizza;
import com.example.model.PizzaVariant;
import com.example.model.enums.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PizzaDaoImpl extends AbstractDao implements PizzaDao {
    @Override
    public Pizza findById(int id) {
        String sql =
                "SELECT p.id, p.name, p.description, p.base_price, p.is_available, p.image_url, z.spicy_level " +
                        "FROM pizzas z " +
                        "JOIN products p ON p.id = z.product_id " +
                        "WHERE z.product_id = ? AND p.type = 'pizza'";
        try {
            return queryOne(sql, ps -> ps.setInt(1, id), this::mapPizza);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Pizza> findAll() {
        String sql =
                "SELECT p.id, p.name, p.description, p.base_price, p.is_available, p.image_url, z.spicy_level " +
                        "FROM pizzas z " +
                        "JOIN products p ON p.id = z.product_id " +
                        "WHERE p.type = 'pizza' " +
                        "ORDER BY p.id";
        try {
            return queryList(sql, null, this::mapPizza);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Pizza> findAvailable() {
        String sql =
                "SELECT p.id, p.name, p.description, p.base_price, p.is_available, p.image_url, z.spicy_level " +
                        "FROM pizzas z " +
                        "JOIN products p ON p.id = z.product_id " +
                        "WHERE p.type = 'pizza' AND p.is_available = TRUE " +
                        "ORDER BY p.id";
        try {
            return queryList(sql, null, this::mapPizza);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Pizza save(Pizza pizza) {
        try {
            // 1) products
            String insProduct =
                    "INSERT INTO products(type, name, description, base_price, is_available, image_url) " +
                            "VALUES ('pizza', ?, ?, ?, ?, ?)";
            int newId = updateReturningId(insProduct, ps -> {
                ps.setString(1, pizza.getName());
                ps.setString(2, pizza.getDescription());
                ps.setBigDecimal(3, pizza.getPrice());
                ps.setBoolean(4, pizza.isAvailable());
                ps.setString(5, pizza.getImageUrl());
            });
            if (newId <= 0) return null;

            // 2) pizzas (spicy_level)
            String insPizza =
                    "INSERT INTO pizzas(product_id, spicy_level) VALUES (?, ?)";
            update(insPizza, ps -> {
                ps.setInt(1, newId);
                String lvl = pizza.getSpicyLevel() == null ? SpicyLevel.mild.name() : pizza.getSpicyLevel().name();
                ps.setString(2, lvl);
            });

            pizza.setId(newId);
            return pizza;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Pizza update(Pizza pizza) {
        try {
            // products
            String sql =
                    "UPDATE products SET name = ?, description = ?, base_price = ?, is_available = ? , image_url = ? " +
                            "WHERE id = ? AND type = 'pizza'";
            int rows = update(sql, ps -> {
                ps.setString(1, pizza.getName());
                ps.setString(2, pizza.getDescription());
                ps.setBigDecimal(3, pizza.getPrice());
                ps.setBoolean(4, pizza.isAvailable());
                ps.setString(5, pizza.getImageUrl());
                ps.setInt(6, pizza.getId());
            });

            String updPizza = "UPDATE pizzas SET spicy_level = ? WHERE product_id = ?";
            update(updPizza, ps -> {
                String lvl = pizza.getSpicyLevel() == null ? SpicyLevel.mild.name() : pizza.getSpicyLevel().name();
                ps.setString(1, lvl);
                ps.setInt(2, pizza.getId());
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

    @Override
    public List<PizzaVariant> findVariants(int pizzaId) {
        String sql = "SELECT id, pizza_id, size, dough, extra_price " +
                "FROM pizza_variants WHERE pizza_id = ? ORDER BY id";
        try {
            return queryList(sql, ps -> ps.setInt(1, pizzaId), this::mapVariant);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // === mappers ===

    private Pizza mapPizza(ResultSet rs) throws SQLException {
        Pizza p = new Pizza();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("base_price"));
        p.setAvailable(rs.getBoolean("is_available"));
        p.setSpicyLevel(SpicyLevel.valueOf(rs.getString("spicy_level")));
        p.setImageUrl(rs.getString("image_url"));
        return p;
    }

    private PizzaVariant mapVariant(ResultSet rs) throws SQLException {
        com.example.model.PizzaVariant v = new com.example.model.PizzaVariant();
        v.setId(rs.getInt("id"));
        v.setPizzaId(rs.getInt("pizza_id"));
        v.setSize(com.example.model.enums.PizzaSize.valueOf(rs.getString("size")));
        v.setDough(com.example.model.enums.DoughType.valueOf(rs.getString("dough")));
        v.setExtraPrice(rs.getBigDecimal("extra_price"));
        return v;
    }
}
