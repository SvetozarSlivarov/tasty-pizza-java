package com.example.dao.impl;

import com.example.dao.OrderDao;
import com.example.dao.base.AbstractDao;
import com.example.model.Order;
import com.example.model.enums.OrderStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl extends AbstractDao implements OrderDao {

    @Override
    public void save(Order order) {
        String sql = "INSERT INTO orders(user_id, status, delivery_phone, delivery_address) VALUES(?, ?, ?, ?)";
        try {
            int id = updateReturningId(sql, ps -> {
                if (order.getUserId() == null) ps.setNull(1, java.sql.Types.INTEGER);
                else ps.setInt(1, order.getUserId());
                ps.setString(2, order.getStatus().name().toLowerCase());
                ps.setString(3, order.getDeliveryPhone());
                ps.setString(4, order.getDeliveryAddress());
            });
            order.setId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Order findById(int id) {
        String sql = """
            SELECT id, user_id, status,
                   ordered_at, preparing_at, out_for_delivery_at, delivered_at, cancelled_at,
                   delivery_phone, delivery_address,
                   created_at, updated_at
              FROM orders WHERE id = ?
            """;
        try {
            return queryOne(sql, ps -> ps.setInt(1, id), this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Order> findByUserId(int userId) {
        String sql = """
            SELECT id, user_id, status,
                   ordered_at, preparing_at, out_for_delivery_at, delivered_at, cancelled_at,
                   delivery_phone, delivery_address,
                   created_at, updated_at
              FROM orders WHERE user_id = ? ORDER BY id DESC
            """;
        try {
            return queryList(sql, ps -> ps.setInt(1, userId), this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean update(Order o) {
        String sql = """
            UPDATE orders SET
                user_id = ?,
                status = ?,
                ordered_at = ?,
                preparing_at = ?,
                out_for_delivery_at = ?,
                delivered_at = ?,
                cancelled_at = ?,
                delivery_phone = ?,
                delivery_address = ?
            WHERE id = ?
            """;
        try {
            return update(sql, ps -> {
                if (o.getUserId() == null) ps.setNull(1, java.sql.Types.INTEGER); else ps.setInt(1, o.getUserId());
                ps.setString(2, o.getStatus().name().toLowerCase());
                ps.setTimestamp(3, o.getOrderedAt());
                ps.setTimestamp(4, o.getPreparingAt());
                ps.setTimestamp(5, o.getOutForDeliveryAt());
                ps.setTimestamp(6, o.getDeliveredAt());
                ps.setTimestamp(7, o.getCancelledAt());
                ps.setString(8, o.getDeliveryPhone());
                ps.setString(9, o.getDeliveryAddress());
                ps.setInt(10, o.getId());
            }) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try {
            return update(sql, ps -> {
                ps.setString(1, status);
                ps.setInt(2, id);
            }) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ---- timestamps ----

    @Override
    public boolean setOrderedNow(int id) {
        String sql = "UPDATE orders SET ordered_at = CURRENT_TIMESTAMP WHERE id = ?";
        try { return update(sql, ps -> ps.setInt(1, id)) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean setPreparingNow(int id) {
        String sql = "UPDATE orders SET preparing_at = CURRENT_TIMESTAMP WHERE id = ?";
        try { return update(sql, ps -> ps.setInt(1, id)) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean setOutForDeliveryNow(int id) {
        String sql = "UPDATE orders SET out_for_delivery_at = CURRENT_TIMESTAMP WHERE id = ?";
        try { return update(sql, ps -> ps.setInt(1, id)) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean setDeliveredNow(int id) {
        String sql = "UPDATE orders SET delivered_at = CURRENT_TIMESTAMP WHERE id = ?";
        try { return update(sql, ps -> ps.setInt(1, id)) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean setCancelledNow(int id) {
        String sql = "UPDATE orders SET cancelled_at = CURRENT_TIMESTAMP WHERE id = ?";
        try { return update(sql, ps -> ps.setInt(1, id)) > 0; }
        catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean setDeliveryInfo(int id, String phone, String address) {
        String sql = "UPDATE orders SET delivery_phone = ?, delivery_address = ? WHERE id = ?";
        try {
            return update(sql, ps -> {
                ps.setString(1, phone);
                ps.setString(2, address);
                ps.setInt(3, id);
            }) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public int deleteGuestCartsOlderThan(Instant cutoff) {
        final String sql =
                "DELETE FROM orders " +
                        "WHERE user_id IS NULL " +
                        "  AND status = ? " +
                        "  AND COALESCE(updated_at, created_at) < ?";

        try {
            return update(sql, ps -> {
                ps.setString(1, "cart");
                ps.setTimestamp(2, Timestamp.from(cutoff));
            });
        } catch (Exception e) {
            throw new RuntimeException("deleteGuestCartsOlderThan failed", e);
        }
    }
    @Override
    public void touch(int orderId) {
        final String sql = "UPDATE orders SET updated_at = CURRENT_TIMESTAMP WHERE id = ?";
        try {
            update(sql, ps -> ps.setInt(1, orderId));
        } catch (Exception e) {
            throw new RuntimeException("orders.touch failed", e);
        }
    }
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try {
            return update(sql, ps -> ps.setInt(1, id)) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // mapper
    private Order map(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));

        int uid = rs.getInt("user_id");
        if (rs.wasNull()) o.setUserId(null); else o.setUserId(uid);

        o.setStatus(OrderStatus.valueOf(rs.getString("status").toUpperCase()));

        o.setOrderedAt(rs.getTimestamp("ordered_at"));
        o.setPreparingAt(rs.getTimestamp("preparing_at"));
        o.setOutForDeliveryAt(rs.getTimestamp("out_for_delivery_at"));
        o.setDeliveredAt(rs.getTimestamp("delivered_at"));
        o.setCancelledAt(rs.getTimestamp("cancelled_at"));

        o.setDeliveryPhone(rs.getString("delivery_phone"));
        o.setDeliveryAddress(rs.getString("delivery_address"));

        o.setCreatedAt(rs.getTimestamp("created_at"));
        o.setUpdatedAt(rs.getTimestamp("updated_at"));
        return o;
    }
}
