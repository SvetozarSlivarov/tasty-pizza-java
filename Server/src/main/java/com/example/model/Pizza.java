package com.example.model;

import java.math.BigDecimal;

public class Pizza {
    private int id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private boolean isAvailable;

    public Pizza() {}

    public Pizza(String name, String description, BigDecimal basePrice, boolean isAvailable) {
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.isAvailable = isAvailable;
    }

    public Pizza(int id, String name, String description, BigDecimal basePrice, boolean isAvailable) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.isAvailable = isAvailable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return basePrice;
    }

    public void setPrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public String toString() {
        return "Pizza{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", basePrice=" + basePrice +
                ", isAvailable=" + isAvailable +
                '}';
    }
}
