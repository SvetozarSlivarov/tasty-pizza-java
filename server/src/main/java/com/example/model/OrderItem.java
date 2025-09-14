package com.example.model;

import java.math.BigDecimal;

public class OrderItem {
    private int id;
    private Order order;
    private int productId;
    private Integer pizzaVariantId;
    private int quantity;
    private BigDecimal unitPrice;
    private String note;

    public OrderItem() {}

    public OrderItem(Order order, int productId, Integer pizzaVariantId,
                     int quantity, BigDecimal unitPrice, String note) {
        this.order = order;
        this.productId = productId;
        this.pizzaVariantId = pizzaVariantId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.note = note;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public Integer getPizzaVariantId() { return pizzaVariantId; }
    public void setPizzaVariantId(Integer pizzaVariantId) { this.pizzaVariantId = pizzaVariantId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + (order != null ? order.getId() : "null") +
                ", productId=" + productId +
                ", pizzaVariantId=" + pizzaVariantId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", note='" + note + '\'' +
                '}';
    }

}