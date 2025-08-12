package com.example.model;

import com.example.model.enums.OrderStatus;
import java.sql.Timestamp;

public class Order {
    private int id;
    private int userId;          // вместо User user
    private Timestamp createdAt;
    private OrderStatus status;

    public Order() {}

    public Order(int userId, OrderStatus status) {
        this.userId = userId;
        this.status = status;
    }

    public Order(int id, int userId, Timestamp createdAt, OrderStatus status) {
        this.id = id;
        this.userId = userId;
        this.createdAt = createdAt;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id){ this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId){ this.userId = userId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt){ this.createdAt = createdAt; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status){ this.status = status; }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", createdAt=" + createdAt +
                ", status=" + status +
                '}';
    }
}
