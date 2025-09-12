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
        try {
            return queryOne(sql, preparedStatement -> preparedStatement.setInt(1, id), this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<IngredientType> findAll() {
        String sql = "SELECT id, name FROM ingredient_types ORDER BY name";
        try {
            return queryList(sql, null, this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean save(IngredientType ingredientType) {
        String sql = "INSERT INTO ingredient_types(name) VALUES(?)";
        try {
            int id = updateReturningId(sql, preparedStatement -> preparedStatement.setString(1, ingredientType.getName()));
            if (id > 0) ingredientType.setId(id);
            return id > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(IngredientType ingredientType) {
        String sql = "UPDATE ingredient_types SET name=? WHERE id=?";
        try {
            return update(sql, preparedStatement -> {
                preparedStatement.setString(1, ingredientType.getName());
                preparedStatement.setInt(2, ingredientType.getId());
            }) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM ingredient_types WHERE id=?";
        try {
            return update(sql, preparedStatement -> preparedStatement.setInt(1, id)) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private IngredientType map(ResultSet resultSet) throws SQLException {
        return new IngredientType(resultSet.getInt("id"), resultSet.getString("name"));
    }
}
