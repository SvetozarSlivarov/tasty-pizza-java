package com.example.dao.impl;

import com.example.dao.IngredientDao;
import com.example.dao.base.AbstractDao;
import com.example.model.Ingredient;
import com.example.model.IngredientType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IngredientDaoImpl extends AbstractDao implements IngredientDao {
    @Override
    public Ingredient findById(int id) {
        String sql = """
            SELECT i.id AS i_id,
                   i.name AS i_name,
                   i.type_id AS i_type_id,
                   i.is_deleted AS i_deleted,
                   i.deleted_at AS i_deleted_at,
                   t.id AS t_id,
                   t.name AS t_name
            FROM ingredients i
            JOIN ingredient_types t ON t.id = i.type_id
            WHERE i.id = ?
        """;
        try {
            return queryOne(sql, ps -> ps.setInt(1, id), this::mapJoin);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Ingredient> findAll() {
        String sql = """
            SELECT i.id AS i_id,
                   i.name AS i_name,
                   i.type_id AS i_type_id,
                   i.is_deleted AS i_deleted,
                   i.deleted_at AS i_deleted_at,
                   t.id AS t_id,
                   t.name AS t_name
            FROM ingredients i
            JOIN ingredient_types t ON t.id = i.type_id
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
            SELECT i.id AS i_id,
                   i.name AS i_name,
                   i.type_id AS i_type_id,
                   i.is_deleted AS i_deleted,
                   i.deleted_at AS i_deleted_at,
                   t.id AS t_id,
                   t.name AS t_name
            FROM ingredients i
            JOIN ingredient_types t ON t.id = i.type_id
            WHERE i.type_id = ?
            ORDER BY i.name
        """;
        try {
            return queryList(sql, ps -> ps.setInt(1, typeId), this::mapJoin);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public int countByTypeId(int typeId) {
        final String sql = "SELECT COUNT(*) FROM ingredients WHERE type_id = ?";
        try {
            return queryOne(sql, ps -> ps.setInt(1, typeId), rs -> rs.getInt(1));
        } catch (Exception e) {
            throw new RuntimeException("countByTypeId_failed for typeId=" + typeId, e);
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
                    i.is_deleted AS i_deleted,
                    i.deleted_at AS i_deleted_at,
                    t.id   AS t_id,
                    t.name AS t_name
            FROM ingredients i
            JOIN ingredient_types t ON t.id = i.type_id
            WHERE i.id IN (""" + placeholders + ") ORDER BY i.name";

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
            int id = updateReturningId(sql, ps -> {
                ps.setString(1, ingredient.getName());
                ps.setInt(2, ingredient.getType().getId());
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
            int rows = update(sql, ps -> {
                ps.setString(1, ingredient.getName());
                ps.setInt(2, ingredient.getType().getId());
                ps.setInt(3, ingredient.getId());
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
            String sql = "UPDATE ingredients SET is_deleted=TRUE, deleted_at=CURRENT_TIMESTAMP WHERE id=?";
            return update(sql, ps -> ps.setInt(1, id)) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Ingredient> findActive() {
        String sql = """
            SELECT i.id AS i_id,
                   i.name AS i_name,
                   i.type_id AS i_type_id,
                   i.is_deleted AS i_deleted,
                   i.deleted_at AS i_deleted_at,
                   t.id AS t_id,
                   t.name AS t_name
            FROM ingredients i
            JOIN ingredient_types t ON t.id = i.type_id
            WHERE i.is_deleted = FALSE
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
    public List<Ingredient> findActiveByTypeId(int typeId) {
        String sql = """
            SELECT i.id AS i_id,
                   i.name AS i_name,
                   i.type_id AS i_type_id,
                   i.is_deleted AS i_deleted,
                   i.deleted_at AS i_deleted_at,
                   t.id AS t_id,
                   t.name AS t_name
            FROM ingredients i
            JOIN ingredient_types t ON t.id = i.type_id
            WHERE i.type_id = ? AND i.is_deleted = FALSE
            ORDER BY i.name
        """;
        try {
            return queryList(sql, ps -> ps.setInt(1, typeId), this::mapJoin);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    @Override
    public List<Ingredient> findActiveByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) return new ArrayList<>();
        ids = ids.stream().distinct().toList();

        String placeholders = String.join(",", java.util.Collections.nCopies(ids.size(), "?"));
        String sql = """
            SELECT  i.id   AS i_id,
                    i.name AS i_name,
                    i.type_id AS i_type_id,
                    i.is_deleted AS i_deleted,
                    i.deleted_at AS i_deleted_at,
                    t.id   AS t_id,
                    t.name AS t_name
            FROM ingredients i
            JOIN ingredient_types t ON t.id = i.type_id
            WHERE i.is_deleted = FALSE AND i.id IN (""" + placeholders + ") ORDER BY i.name";

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
    public boolean restore(int id) {
        try {
            String sql = "UPDATE ingredients SET is_deleted=FALSE, deleted_at=NULL WHERE id=?";
            return update(sql, ps -> ps.setInt(1, id)) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Ingredient mapJoin(ResultSet rs) throws SQLException {
        IngredientType type = new IngredientType(rs.getInt("t_id"), rs.getString("t_name"));

        Ingredient ing = new Ingredient(rs.getInt("i_id"), rs.getString("i_name"), type);

        try {
            ing.setDeleted(rs.getBoolean("i_deleted"));
        } catch (SQLException ignore) { /* ако колоната още не е мигрирана */ }

        try {
            Timestamp ts = rs.getTimestamp("i_deleted_at");
            if (ts != null && hasSetterDeletedAt(ing)) {;
            }
        } catch (SQLException ignore) { }

        return ing;
    }
    private boolean hasSetterDeletedAt(Ingredient ing) {
        try {
            ing.getClass().getMethod("setDeletedAt", Timestamp.class);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
