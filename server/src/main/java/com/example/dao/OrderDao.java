package com.example.dao;

import com.example.model.Order;

import java.time.Instant;
import java.util.List;

public interface OrderDao {
    // create
    void save(Order order);

    // read
    Order findById(int id);
    List<Order> findByUserId(int userId);

    boolean update(Order order);

    boolean updateStatus(int id, String status);

    boolean setOrderedNow(int id);
    boolean setPreparingNow(int id);
    boolean setOutForDeliveryNow(int id);
    boolean setDeliveredNow(int id);
    boolean setCancelledNow(int id);

    boolean setDeliveryInfo(int id, String phone, String address);

    void touch(int orderId);

    int deleteGuestCartsOlderThan(Instant cutoff);

    boolean delete(int id);
}
