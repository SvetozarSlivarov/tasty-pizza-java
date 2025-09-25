package com.example.dao.impl;

import com.example.dao.OrderItemDao;
import com.example.dao.base.AbstractDao;
import com.example.model.Order;
import com.example.model.OrderItem;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.INTEGER;

public class OrderItemDaoImpl extends AbstractDao implements OrderItemDao {

    @Override
    public OrderItem findById(int id) {
        String sql = "SELECT id, order_id, product_id, pizza_variant_id, quantity, unit_price, note FROM order_items WHERE id = ?";
        try {
            return queryOne(sql, ps -> ps.setInt(1, id), this::map);
        } catch (SQLException e) {
            e.printStackTrace(); return null;
        }
    }

    @Override
    public List<OrderItem> findByOrderId(int orderId) {
        String sql = "SELECT id, order_id, product_id, pizza_variant_id, quantity, unit_price, note FROM order_items WHERE order_id = ? ORDER BY id";
        try {
            return queryList(sql, ps -> ps.setInt(1, orderId), this::map);
        } catch (SQLException e) {
            e.printStackTrace(); return new ArrayList<>();
        }
    }

    @Override
    public boolean save(OrderItem item) {
        String sql = "INSERT INTO order_items(order_id, product_id, pizza_variant_id, quantity, unit_price, note) VALUES(?,?,?,?,?,?)";
        try {
            int newId = updateReturningId(sql, ps -> {
                ps.setInt(1, item.getOrder().getId());
                ps.setInt(2, item.getProductId());
                if (item.getPizzaVariantId() == null) ps.setNull(3, INTEGER); else ps.setInt(3, item.getPizzaVariantId());
                ps.setInt(4, item.getQuantity());
                ps.setBigDecimal(5, item.getUnitPrice() != null ? item.getUnitPrice() : BigDecimal.ZERO);
                ps.setString(6, item.getNote());
            });
            item.setId(newId);
            return newId > 0;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    @Override
    public boolean updateQuantity(int id, int qty) {
        String sql = "UPDATE order_items SET quantity = ? WHERE id = ?";
        try {
            return update(sql, ps -> { ps.setInt(1, qty); ps.setInt(2, id); }) > 0;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM order_items WHERE id = ?";
        try {
            return update(sql, ps -> ps.setInt(1, id)) > 0;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    public boolean updateVariantAndPrice(int id, Integer variantId, BigDecimal unitPrice) {
        String sql = "UPDATE order_items SET pizza_variant_id=?, unit_price=? WHERE id=?";
        try {
            return update(sql, ps -> {
                if (variantId == null) ps.setNull(1, INTEGER); else ps.setInt(1, variantId);
                ps.setBigDecimal(2, unitPrice);
                ps.setInt(3, id);
            }) == 1;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean updateNote(int id, String note) {
        String sql = "UPDATE order_items SET note=? WHERE id=?";
        try {
            return update(sql, ps -> { ps.setString(1, note); ps.setInt(2, id); }) == 1;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private OrderItem map(ResultSet rs) throws SQLException {
        Order order = new Order(); order.setId(rs.getInt("order_id"));
        OrderItem item = new OrderItem();
        item.setId(rs.getInt("id"));
        item.setOrder(order);
        item.setProductId(rs.getInt("product_id"));
        int pv = rs.getInt("pizza_variant_id");
        item.setPizzaVariantId(rs.wasNull() ? null : pv);
        item.setQuantity(rs.getInt("quantity"));
        item.setUnitPrice(rs.getBigDecimal("unit_price"));
        item.setNote(rs.getString("note"));
        return item;
    }
}
