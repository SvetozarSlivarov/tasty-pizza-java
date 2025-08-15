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
        String sql = "SELECT * FROM drinks WHERE id=?";
        try {
            return queryOne(sql, preparedStatement -> preparedStatement.setInt(1, id), this::map);
        } catch (SQLException e)
        { e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Drink> findAll() {
        String sql = "SELECT * FROM drinks ORDER BY id";
        try {
            return queryList(sql, null, this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    @Override
    public List<Drink> findAvailable() {
        String sql = "SELECT * FROM drinks WHERE is_available = true";
        try {
            return queryList(sql, null, this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public Drink save(Drink drink) {
        String sql = "INSERT INTO drinks(name, description, price, is_available) VALUES(?,?,?,?)";
        try {
            int id = updateReturningId(sql, ps -> {
                ps.setString(1, drink.getName());
                ps.setString(2, drink.getDescription());
                ps.setBigDecimal(3, drink.getPrice());
                ps.setBoolean(4, drink.isAvailable());
            });
            if (id <= 0) return null;
            drink.setId(id);
            return drink;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Drink update(Drink drink) {
        String sql = "UPDATE drinks SET name=?, description=?, price=?, is_available=? WHERE id=?";
        try {
            int rows = update(sql, ps -> {
                ps.setString(1, drink.getName());
                ps.setString(2, drink.getDescription());
                ps.setBigDecimal(3, drink.getPrice());
                ps.setBoolean(4, drink.isAvailable());
                ps.setInt(5, drink.getId());
            });
            return rows > 0 ? drink : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Drink delete(int id) {
        try {
            Drink existing = findById(id);
            if (existing == null) return null;
            String sql = "DELETE FROM drinks WHERE id=?";
            int rows = update(sql, ps -> ps.setInt(1, id));
            return rows > 0 ? existing : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private Drink map(ResultSet resultSet) throws SQLException {
        Drink drink = new Drink();
        drink.setId(resultSet.getInt("id"));
        drink.setName(resultSet.getString("name"));
        drink.setDescription(resultSet.getString("description"));
        drink.setPrice(resultSet.getBigDecimal("price"));
        drink.setAvailable(resultSet.getBoolean("is_available"));
        return drink;
    }
}
