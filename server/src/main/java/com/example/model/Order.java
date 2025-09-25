package com.example.model;

import com.example.model.enums.OrderStatus;

import java.sql.Timestamp;

public class Order {
    private int id;
    private Integer userId;
    private OrderStatus status = OrderStatus.CART;

    private Timestamp orderedAt;
    private Timestamp preparingAt;
    private Timestamp outForDeliveryAt;
    private Timestamp deliveredAt;
    private Timestamp cancelledAt;

    private String deliveryPhone;
    private String deliveryAddress;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public Timestamp getOrderedAt() { return orderedAt; }
    public void setOrderedAt(Timestamp orderedAt) { this.orderedAt = orderedAt; }

    public Timestamp getPreparingAt() { return preparingAt; }
    public void setPreparingAt(Timestamp preparingAt) { this.preparingAt = preparingAt; }

    public Timestamp getOutForDeliveryAt() { return outForDeliveryAt; }
    public void setOutForDeliveryAt(Timestamp outForDeliveryAt) { this.outForDeliveryAt = outForDeliveryAt; }

    public Timestamp getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(Timestamp deliveredAt) { this.deliveredAt = deliveredAt; }

    public Timestamp getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Timestamp cancelledAt) { this.cancelledAt = cancelledAt; }

    public String getDeliveryPhone() { return deliveryPhone; }
    public void setDeliveryPhone(String deliveryPhone) { this.deliveryPhone = deliveryPhone; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
