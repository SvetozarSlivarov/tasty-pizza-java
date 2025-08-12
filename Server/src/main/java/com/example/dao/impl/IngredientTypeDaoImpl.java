package com.example.dao.impl;

import com.example.dao.IngredientTypeDao;
import com.example.dao.base.AbstractDao;
import com.example.model.IngredientType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredientTypeDaoImpl extends AbstractDao implements IngredientTypeDao {
    @Override
    public IngredientType findById(int id) {
        String sql = "SELECT id, name FROM ingredient_types WHERE id=?";
        try { return queryOne(sql, ps -> ps.setInt(1, id), this::map); }
        catch (SQLException e) { e.printStackTrace(); return null; }
    }

    @Override
    public List<IngredientType> findAll() {
        String sql = "SELECT id, name FROM ingredient_types ORDER BY name";
        try { return queryList(sql, null, this::map); }
        catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    @Override
    public boolean save(IngredientType t) {
        String sql = "INSERT INTO ingredient_types(name) VALUES(?)";
        try {
            int id = updateReturningId(sql, ps -> ps.setString(1, t.getName()));
            if (id > 0) t.setId(id);
            return id > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean update(IngredientType t) {
        String sql = "UPDATE ingredient_types SET name=? WHERE id=?";
        try { return update(sql, ps -> { ps.setString(1, t.getName()); ps.setInt(2, t.getId()); }) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        try { return update("DELETE FROM ingredient_types WHERE id=?", ps -> ps.setInt(1, id)) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private IngredientType map(ResultSet rs) throws SQLException {
        return new IngredientType(rs.getInt("id"), rs.getString("name"));
    }
}
