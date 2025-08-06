package com.example.model;

import com.example.model.enums.OrderStatus;

import java.sql.Timestamp;

public class Order {
    private int id;
    private User user;
    private Timestamp createdAt;
    private OrderStatus status;

    public Order() {}

    public Order(User user, OrderStatus status) {
        this.user = user;
        this.status = status;
    }

    public Order(int id, User user, Timestamp createdAt, OrderStatus status) {
        this.id = id;
        this.user = user;
        this.createdAt = createdAt;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user){
        this.user = user;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt){
        this.createdAt = createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status){
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", user=" + (user != null ? user.getUsername() : "null") +
                ", createdAt=" + createdAt +
                ", status='" + status + '\'' +
                '}';
    }
}
