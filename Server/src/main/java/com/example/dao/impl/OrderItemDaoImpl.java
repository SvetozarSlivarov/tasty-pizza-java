package com.example.dao.impl;

import com.example.dao.OrderItemDao;
import com.example.dao.base.AbstractDao;
import com.example.model.Order;
import com.example.model.OrderItem;
import com.example.model.enums.ProductType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDaoImpl extends AbstractDao implements OrderItemDao {

    @Override
    public OrderItem findById(int id) {
        String sql = "SELECT id, order_id, product_type, product_id, quantity FROM order_items WHERE id = ?";
        try {
            return queryOne(sql, preparedStatement -> preparedStatement.setInt(1, id), this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<OrderItem> findByOrderId(int orderId) {
        String sql = "SELECT id, order_id, product_type, product_id, quantity FROM order_items WHERE order_id = ? ORDER BY id";
        try {
            return queryList(sql, preparedStatement -> preparedStatement.setInt(1, orderId), this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public boolean save(OrderItem item) {
        String sql = "INSERT INTO order_items(order_id, product_type, product_id, quantity) VALUES(?,?,?,?)";
        try {
            int newId = updateReturningId(sql, preparedStatement -> {
                preparedStatement.setInt(1, item.getOrder().getId());
                preparedStatement.setString(2, item.getProductType().name());
                preparedStatement.setInt(3, item.getProductId());
                preparedStatement.setInt(4, item.getQuantity());
            });
            if (newId > 0) item.setId(newId);
            return newId > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateQuantity(int id, int qty) {
        String sql = "UPDATE order_items SET quantity = ? WHERE id = ?";
        try {
            return update(sql, preparedStatement -> {
                preparedStatement.setInt(1, qty);
                preparedStatement.setInt(2, id);
            }) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM order_items WHERE id = ?";
        try {
            return update(sql, preparedStatement -> preparedStatement.setInt(1, id)) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private OrderItem map(ResultSet resultSet) throws SQLException {
        Order order = new Order();
        order.setId(resultSet.getInt("order_id"));

        OrderItem item = new OrderItem();
        item.setId(resultSet.getInt("id"));
        item.setOrder(order);
        item.setProductType(ProductType.valueOf(resultSet.getString("product_type").toUpperCase()));
        item.setProductId(resultSet.getInt("product_id"));
        item.setQuantity(resultSet.getInt("quantity"));
        return item;
    }
}
