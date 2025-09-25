package com.example.dao.impl;

import com.example.dao.ProductDao;
import com.example.db.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProductDaoImpl implements ProductDao {

    @Override
    public BigDecimal findBasePriceById(Connection cx, int productId) throws Exception {
        final String sql = "SELECT base_price FROM products WHERE id=?";
        try (PreparedStatement ps = cx.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new IllegalArgumentException("Invalid product_id: " + productId);
                return rs.getBigDecimal(1);
            }
        }
    }
    @Override
    public String findTypeById(int productId) {
        final String sql = "SELECT type FROM products WHERE id=?";
        try (Connection cx = DBConnection.getConnection();
             PreparedStatement ps = cx.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return rs.getString(1);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve product type for id=" + productId, e);
        }
    }
}
