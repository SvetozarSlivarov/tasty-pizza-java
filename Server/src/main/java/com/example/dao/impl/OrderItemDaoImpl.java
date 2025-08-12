package com.example.dao.impl;

import com.example.dao.OrderItemDao;
import com.example.db.DBConnection;
import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.model.enums.ProductType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDaoImpl implements OrderItemDao {

    @Override
    public OrderItem findById(int id) {
        String sql = "SELECT * FROM order_items WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? extract(rs) : null;
            }
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    @Override
    public List<OrderItem> findByOrderId(int orderId) {
        String sql = "SELECT * FROM order_items WHERE order_id=? ORDER BY id";
        List<OrderItem> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(extract(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean save(OrderItem item) {
        String sql = """
            INSERT INTO order_items(order_id, product_type, product_id, quantity)
            VALUES(?,?,?,?)
        """;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, item.getOrder().getId());
            ps.setString(2, item.getProductType().name()); // PIZZA/DRINK
            ps.setInt(3, item.getProductId());
            ps.setInt(4, item.getQuantity());
            int rows = ps.executeUpdate();
            if (rows > 0) try (ResultSet k = ps.getGeneratedKeys()) { if (k.next()) item.setId(k.getInt(1)); }
            return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM order_items WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private OrderItem extract(ResultSet rs) throws SQLException {
        OrderItem oi = new OrderItem();
        oi.setId(rs.getInt("id"));

        Order o = new Order();
        o.setId(rs.getInt("order_id"));
        oi.setOrder(o);

        String pt = rs.getString("product_type");
        oi.setProductType(ProductType.valueOf(pt.toUpperCase()));

        oi.setProductId(rs.getInt("product_id"));
        oi.setQuantity(rs.getInt("quantity"));
        return oi;
    }
}
