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
        try {
            return queryOne(sql, preparedStatement -> preparedStatement.setInt(1, id), this::mapJoin);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Ingredient> findAll() {
        String sql = """
            SELECT i.id AS i_id, i.name AS i_name, i.type_id AS i_type_id,
                   t.id AS t_id, t.name AS t_name
            FROM ingredients i JOIN ingredient_types t ON t.id = i.type_id
            ORDER BY i.name
        """;
        try {
            return queryList(sql, null, this::mapJoin);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
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
        try {
            return queryList(sql, preparedStatement -> preparedStatement.setInt(1, typeId), this::mapJoin);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    @Override
    public List<Ingredient> findByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();

        ids = ids.stream().distinct().toList();

        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        String sql = """
        SELECT  i.id   AS i_id,
                i.name AS i_name,
                i.type_id AS i_type_id,
                t.id   AS t_id,
                t.name AS t_name
        FROM ingredients i
        JOIN ingredient_types t ON t.id = i.type_id
        WHERE i.id IN (""" + placeholders + ") " +
                "ORDER BY i.name";

        try {
            List<Integer> finalIds = ids;
            return queryList(sql, ps -> {
                int idx = 1;
                for (Integer id : finalIds) ps.setInt(idx++, id);
            }, this::mapJoin);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
        @Override
        public boolean save(Ingredient ingredient) {
            String sql = "INSERT INTO ingredients(name, type_id) VALUES(?,?)";
            try {
                int id = updateReturningId(sql, preparedStatement -> {
                    preparedStatement.setString(1, ingredient.getName());
                    preparedStatement.setInt(2, ingredient.getType().getId());
                });
                if (id > 0) ingredient.setId(id);
                return id > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    
        @Override
        public boolean update(Ingredient ingredient) {
            String sql = "UPDATE ingredients SET name=?, type_id=? WHERE id=?";
            try {
                int rows = update(sql, preparedStatement -> {
                    preparedStatement.setString(1, ingredient.getName());
                    preparedStatement.setInt(2, ingredient.getType().getId());
                    preparedStatement.setInt(3, ingredient.getId());
                });
                return rows > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
    
        @Override
        public boolean delete(int id) {
            try {
                String sql = "DELETE FROM ingredients WHERE id=?";
                return update(sql, preparedStatement -> preparedStatement.setInt(1, id)) > 0;
            } catch (SQLException e)
            { e.printStackTrace();
                return false;
            }
        }
    
        private Ingredient mapJoin(ResultSet resultSet) throws SQLException {
            var type = new com.example.model.IngredientType(resultSet.getInt("t_id"),
                    resultSet.getString("t_name"));
            return new com.example.model.Ingredient(resultSet.getInt("i_id"),
                    resultSet.getString("i_name"), type);
        }
    }
    
    
