package com.example.dao;

import com.example.model.OrderItem;
import java.util.List;

public interface OrderItemDao {
    OrderItem findById(int id);
    List<OrderItem> findByOrderId(int orderId);
    boolean save(OrderItem item);
    boolean updateQuantity(int id, int qty);
    boolean delete(int id);
}
