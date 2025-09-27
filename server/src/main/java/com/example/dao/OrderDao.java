package com.example.dao;

import com.example.model.Order;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

public interface OrderDao {
    void save(Order order);
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

    List<Order> search(String status, String q, Timestamp from, Timestamp to, String sort, int limit, int offset);
}
