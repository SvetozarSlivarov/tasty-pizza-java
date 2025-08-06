package com.example.model;

import com.example.model.enums.ProductType;

public class OrderItem {
    private int id;
    private Order order;
    private ProductType productType;
    private int productId;
    private int quantity;

    public OrderItem() {}

    public OrderItem(Order order, ProductType productType, int productId, int quantity) {
        this.order = order;
        this.productType = productType;
        this.productId = productId;
        this.quantity = quantity;
    }

    public OrderItem(int id, Order order, ProductType productType, int productId, int quantity) {
        this.id = id;
        this.order = order;
        this.productType = productType;
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order){
        this.order = order;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType){
        this.productType = productType;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId){
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderId=" + (order != null ? order.getId() : "null") +
                ", productType='" + productType + '\'' +
                ", productId=" + productId +
                ", quantity=" + quantity +
                '}';
    }
}
