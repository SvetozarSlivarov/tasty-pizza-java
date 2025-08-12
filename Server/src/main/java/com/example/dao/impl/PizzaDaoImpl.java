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
        try { return queryList(sql, null, this::map); }
        catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
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
    public boolean save(Pizza p) {
        String sql = "INSERT INTO pizzas(name, description, base_price, is_available) VALUES(?,?,?,?)";
        try {
            int id = updateReturningId(sql, ps -> {
                ps.setString(1, p.getName());
                ps.setString(2, p.getDescription());
                ps.setBigDecimal(3, p.getPrice());
                ps.setBoolean(4, p.isAvailable());
            });
            if (id > 0) p.setId(id);
            return id > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean update(Pizza p) {
        String sql = "UPDATE pizzas SET name=?, description=?, base_price=?, is_available=? WHERE id=?";
        try {
            int rows = update(sql, ps -> {
                ps.setString(1, p.getName());
                ps.setString(2, p.getDescription());
                ps.setBigDecimal(3, p.getPrice());
                ps.setBoolean(4, p.isAvailable());
                ps.setInt(5, p.getId());
            });
            return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        try { return update("DELETE FROM pizzas WHERE id=?", ps -> ps.setInt(1, id)) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Pizza map(ResultSet rs) throws SQLException {
        Pizza p = new Pizza();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("base_price"));
        p.setAvailable(rs.getBoolean("is_available"));
        p.setCreatedAt(rs.getTimestamp("created_at"));
        return p;
    }
}
