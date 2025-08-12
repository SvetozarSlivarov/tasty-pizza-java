package com.example.dao.impl;

import com.example.dao.IngredientTypeDao;
import com.example.db.DBConnection;
import com.example.model.IngredientType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredientTypeDaoImpl implements IngredientTypeDao {

    @Override
    public IngredientType findById(int id) {
        String sql = "SELECT id, name FROM ingredient_types WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extract(rs) : null;
            }
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    @Override
    public List<IngredientType> findAll() {
        String sql = "SELECT id, name FROM ingredient_types ORDER BY name";
        List<IngredientType> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean save(IngredientType t) {
        String sql = "INSERT INTO ingredient_types(name) VALUES(?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, t.getName());
            int rows = ps.executeUpdate();
            if (rows > 0) try (ResultSet k = ps.getGeneratedKeys()) { if (k.next()) t.setId(k.getInt(1)); }
            return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean update(IngredientType t) {
        String sql = "UPDATE ingredient_types SET name=? WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, t.getName());
            ps.setInt(2, t.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM ingredient_types WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private IngredientType extract(ResultSet rs) throws SQLException {
        return new IngredientType(rs.getInt("id"), rs.getString("name"));
    }
}
