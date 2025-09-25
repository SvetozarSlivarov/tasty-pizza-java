package com.example.dao.impl;

import com.example.dao.PizzaVariantDao;
import com.example.dao.base.AbstractDao;
import com.example.model.enums.DoughType;
import com.example.model.enums.PizzaSize;
import com.example.model.PizzaVariant;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PizzaVariantDaoImpl extends AbstractDao implements PizzaVariantDao {

    @Override
    public PizzaVariant findById(int id) {
        String sql = "SELECT id, pizza_id, size, dough, extra_price FROM pizza_variants WHERE id = ?";
        try {
            return queryOne(sql, ps -> ps.setInt(1, id), this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<PizzaVariant> findByPizzaId(int pizzaId) {
        String sql = "SELECT id, pizza_id, size, dough, extra_price FROM pizza_variants WHERE pizza_id = ? ORDER BY id";
        try {
            return queryList(sql, ps -> ps.setInt(1, pizzaId), this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public PizzaVariant save(PizzaVariant v) {
        String sql = "INSERT INTO pizza_variants(pizza_id, size, dough, extra_price) VALUES (?, ?, ?, ?)";
        try {
            int id = updateReturningId(sql, ps -> {
                ps.setInt(1, v.getPizzaId());
                ps.setString(2, v.getSize().name());
                ps.setString(3, v.getDough().name());
                ps.setBigDecimal(4, v.getExtraPrice());
            });
            if (id <= 0) return null;
            v.setId(id);
            return v;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public PizzaVariant update(PizzaVariant v) {
        String sql = "UPDATE pizza_variants SET size = ?, dough = ?, extra_price = ? WHERE id = ?";
        try {
            int rows = update(sql, ps -> {
                ps.setString(1, v.getSize().name());
                ps.setString(2, v.getDough().name());
                ps.setBigDecimal(3, v.getExtraPrice());
                ps.setInt(4, v.getId());
            });
            return rows > 0 ? v : null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean delete(int id) {
        try {
            int rows = update("DELETE FROM pizza_variants WHERE id = ?", ps -> ps.setInt(1, id));
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public int deleteAllByPizzaId(int pizzaId) {
        try {
            return update("DELETE FROM pizza_variants WHERE pizza_id = ?", ps -> ps.setInt(1, pizzaId));
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int bulkInsert(List<PizzaVariant> variants) {
        if (variants == null || variants.isEmpty()) return 0;
        int count = 0;
        for (PizzaVariant v : variants) {
            if (save(v) != null) count++;
        }
        return count;
    }


    private PizzaVariant map(ResultSet rs) throws SQLException {
        PizzaVariant v = new PizzaVariant();
        v.setId(rs.getInt("id"));
        v.setPizzaId(rs.getInt("pizza_id"));
        v.setSize(PizzaSize.valueOf(rs.getString("size")));
        v.setDough(DoughType.valueOf(rs.getString("dough")));
        v.setExtraPrice(rs.getBigDecimal("extra_price"));
        return v;
    }
}
