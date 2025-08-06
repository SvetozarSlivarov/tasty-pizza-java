package com.example.dao;

import com.example.model.Order;
import java.util.List;

public interface OrderDao {
    void save(Order order);
    Order findById(int id);
    List<Order> findByUserId(int userId);
}
