package com.example.dao.impl;

import com.example.dao.OrderDao;
import com.example.dao.base.AbstractDao;
import com.example.model.Order;
import com.example.model.enums.OrderStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl extends AbstractDao implements OrderDao {

    @Override
    public void save(Order order) {
        String sql = "INSERT INTO orders(user_id, status) VALUES(?, ?)";
        try {
            int id = updateReturningId(sql, ps -> {
                if (order.getUserId() == null) {
                    ps.setNull(1, java.sql.Types.INTEGER);
                } else {
                    ps.setInt(1, order.getUserId());
                }
                ps.setString(2, order.getStatus().name().toLowerCase());
            });
            order.setId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Order findById(int id) {
        String sql = "SELECT id, user_id, created_at, status FROM orders WHERE id = ?";
        try {
            return queryOne(sql, preparedStatement -> preparedStatement.setInt(1, id), this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Order> findByUserId(int userId) {
        String sql = "SELECT id, user_id, created_at, status FROM orders WHERE user_id = ? ORDER BY id";
        try {
            return queryList(sql, preparedStatement -> preparedStatement.setInt(1, userId), this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try {
            return update(sql, preparedStatement -> {
                preparedStatement.setString(1, status);
                preparedStatement.setInt(2, id);
            }) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try {
            return update(sql, preparedStatement -> preparedStatement.setInt(1, id)) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // mapper
    private Order map(ResultSet resultSet) throws SQLException {
        Order o = new Order();
        o.setId(resultSet.getInt("id"));
        int uid = resultSet.getInt("user_id");
        if (resultSet.wasNull()) {
            o.setUserId(null);
        } else {
            o.setUserId(uid);
        }
        o.setCreatedAt(resultSet.getTimestamp("created_at"));
        o.setStatus(OrderStatus.valueOf(resultSet.getString("status").toUpperCase()));
        return o;
    }
}
