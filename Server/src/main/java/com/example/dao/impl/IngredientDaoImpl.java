package com.example.dao.impl;

import com.example.dao.IngredientDao;
import com.example.dao.base.AbstractDao;
import com.example.model.Ingredient;
import com.example.model.IngredientType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredientDaoImpl extends AbstractDao implements IngredientDao {
    @Override
    public Ingredient findById(int id) {
        String sql = """
            SELECT i.id AS i_id, i.name AS i_name, i.type_id AS i_type_id,
                   t.id AS t_id, t.name AS t_name
            FROM ingredients i JOIN ingredient_types t ON t.id = i.type_id
            WHERE i.id = ?
        """;
        try { return queryOne(sql, ps -> ps.setInt(1, id), this::mapJoin); }
        catch (SQLException e) { e.printStackTrace(); return null; }
    }

    @Override
    public List<Ingredient> findAll() {
        String sql = """
            SELECT i.id AS i_id, i.name AS i_name, i.type_id AS i_type_id,
                   t.id AS t_id, t.name AS t_name
            FROM ingredients i JOIN ingredient_types t ON t.id = i.type_id
            ORDER BY i.name
        """;
        try { return queryList(sql, null, this::mapJoin); }
        catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    @Override
    public List<Ingredient> findByTypeId(int typeId) {
        String sql = """
            SELECT i.id AS i_id, i.name AS i_name, i.type_id AS i_type_id,
                   t.id AS t_id, t.name AS t_name
            FROM ingredients i JOIN ingredient_types t ON t.id = i.type_id
            WHERE i.type_id = ?
            ORDER BY i.name
        """;
        try { return queryList(sql, ps -> ps.setInt(1, typeId), this::mapJoin); }
        catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    @Override
    public boolean save(Ingredient ing) {
        String sql = "INSERT INTO ingredients(name, type_id) VALUES(?,?)";
        try {
            int id = updateReturningId(sql, ps -> {
                ps.setString(1, ing.getName());
                ps.setInt(2, ing.getType().getId());
            });
            if (id > 0) ing.setId(id);
            return id > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean update(Ingredient ing) {
        String sql = "UPDATE ingredients SET name=?, type_id=? WHERE id=?";
        try {
            int rows = update(sql, ps -> {
                ps.setString(1, ing.getName());
                ps.setInt(2, ing.getType().getId());
                ps.setInt(3, ing.getId());
            });
            return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        try { return update("DELETE FROM ingredients WHERE id=?", ps -> ps.setInt(1, id)) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private Ingredient mapJoin(ResultSet rs) throws SQLException {
        var type = new com.example.model.IngredientType(rs.getInt("t_id"), rs.getString("t_name"));
        return new com.example.model.Ingredient(rs.getInt("i_id"), rs.getString("i_name"), type);
    }
}


