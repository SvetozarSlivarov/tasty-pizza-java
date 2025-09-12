package com.example.dao.impl;

import com.example.dao.DrinkDao;
import com.example.dao.base.AbstractDao;
import com.example.model.Drink;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DrinkDaoImpl extends AbstractDao implements DrinkDao {
    @Override
    public Drink findById(int id) {
        String sql = "SELECT p.id, p.name, p.description, p.base_price AS price, p.is_available, " +
                "       p.image_url AS image_url " +
                "FROM drinks d JOIN products p ON p.id = d.product_id " +
                "WHERE d.product_id=? AND p.type='drink'";
        try {
            return queryOne(sql, ps -> ps.setInt(1, id), this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Drink> findAll() {
        String sql = "SELECT p.id, p.name, p.description, p.base_price AS price, p.is_available, " +
                "       p.image_url AS image_url " +
                "FROM drinks d JOIN products p ON p.id = d.product_id " +
                "WHERE p.type='drink'";
        try {
            return queryList(sql, null, this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public List<Drink> findAvailable() {
        String sql = "SELECT p.id, p.name, p.description, p.base_price AS price, p.is_available, " +
                "       p.image_url AS image_url " +
                "FROM drinks d JOIN products p ON p.id = d.product_id " +
                "WHERE p.type='drink' AND p.is_available=TRUE";
        try {
            return queryList(sql, null, this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Drink save(Drink drink) {
        try {
            // 1) insert into products
            String insProduct = "INSERT INTO products(type, name, description, base_price, is_available, image_url) " +
                    "VALUES ('drink', ?, ?, ?, ?, ?)";
            int id = updateReturningId(insProduct, ps -> {
                ps.setString(1, drink.getName());
                ps.setString(2, drink.getDescription());
                ps.setBigDecimal(3, drink.getPrice());
                ps.setBoolean(4, drink.isAvailable());
                ps.setString(5, drink.getImageUrl());
            });
            if (id <= 0) return null;

            String insDrink = "INSERT INTO drinks(product_id) VALUES (?)";
            update(insDrink, ps -> ps.setInt(1, id));

            drink.setId(id);
            return drink;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Drink update(Drink drink) {
        String sql = "UPDATE products SET name=?, description=?, base_price=?, is_available=?, image_url=? WHERE id=? AND type='drink'";
        try {
            int rows = update(sql, ps -> {
                ps.setString(1, drink.getName());
                ps.setString(2, drink.getDescription());
                ps.setBigDecimal(3, drink.getPrice());
                ps.setBoolean(4, drink.isAvailable());
                ps.setString(5, drink.getImageUrl());
                ps.setInt(6, drink.getId());
            });
            return rows > 0 ? drink : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Drink delete(int id) {
        Drink existing = findById(id);
        if (existing == null) return null;
        try {
            int rows = update("DELETE FROM products WHERE id=? AND type='drink'", ps -> ps.setInt(1, id));
            return rows > 0 ? existing : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Drink map(ResultSet rs) throws SQLException {
        Drink d = new Drink();
        d.setId(rs.getInt("id"));
        d.setName(rs.getString("name"));
        d.setDescription(rs.getString("description"));
        d.setPrice(rs.getBigDecimal("price"));
        d.setAvailable(rs.getBoolean("is_available"));
        d.setImageUrl(rs.getString("image_url"));
        return d;
    }
}
