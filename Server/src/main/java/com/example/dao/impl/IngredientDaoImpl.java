package com.example.dao.impl;

import com.example.dao.IngredientDao;
import com.example.db.DBConnection;
import com.example.model.Ingredient;
import com.example.model.IngredientType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredientDaoImpl implements IngredientDao {

    @Override
    public Ingredient findById(int id) {
        final String sql = """
            SELECT i.id AS i_id, i.name AS i_name, i.type_id AS i_type_id,
                   t.id AS t_id, t.name AS t_name
            FROM ingredients i
            JOIN ingredient_types t ON t.id = i.type_id
            WHERE i.id = ?
        """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extract(rs) : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Ingredient> findAll() {
        final String sql = """
            SELECT i.id AS i_id, i.name AS i_name, i.type_id AS i_type_id,
                   t.id AS t_id, t.name AS t_name
            FROM ingredients i
            JOIN ingredient_types t ON t.id = i.type_id
            ORDER BY i.name
        """;
        List<Ingredient> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(extract(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Ingredient> findByTypeId(int typeId) {
        final String sql = """
            SELECT i.id AS i_id, i.name AS i_name, i.type_id AS i_type_id,
                   t.id AS t_id, t.name AS t_name
            FROM ingredients i
            JOIN ingredient_types t ON t.id = i.type_id
            WHERE i.type_id = ?
            ORDER BY i.name
        """;
        List<Ingredient> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, typeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(extract(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean save(Ingredient ingredient) {
        final String sql = "INSERT INTO ingredients(name, type_id) VALUES(?, ?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, ingredient.getName());
            ps.setInt(2, ingredient.getType().getId());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) ingredient.setId(keys.getInt(1));
                }
            }
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Ingredient ingredient) {
        final String sql = "UPDATE ingredients SET name = ?, type_id = ? WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, ingredient.getName());
            ps.setInt(2, ingredient.getType().getId());
            ps.setInt(3, ingredient.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        final String sql = "DELETE FROM ingredients WHERE id = ?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Ingredient extract(ResultSet rs) throws SQLException {
        IngredientType type = new IngredientType();
        type.setId(rs.getInt("t_id"));
        type.setName(rs.getString("t_name"));

        return new Ingredient(
                rs.getInt("i_id"),
                rs.getString("i_name"),
                type
        );
    }
}
