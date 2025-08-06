package com.example.model;

import com.example.model.enums.CustomizationAction;

public class OrderCustomization {
    private int id;
    private OrderItem orderItem;
    private Ingredient ingredient;
    private CustomizationAction action;

    public OrderCustomization() {}

    public OrderCustomization(OrderItem orderItem, Ingredient ingredient, CustomizationAction action) {
        this.orderItem = orderItem;
        this.ingredient = ingredient;
        this.action = action;
    }

    public OrderCustomization(int id, OrderItem orderItem, Ingredient ingredient, CustomizationAction action) {
        this.id = id;
        this.orderItem = orderItem;
        this.ingredient = ingredient;
        this.action = action;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public OrderItem getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(OrderItem orderItem){
        this.orderItem = orderItem;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient){
        this.ingredient = ingredient;
    }

    public CustomizationAction getAction() {
        return action;
    }

    public void setAction(CustomizationAction action){
        this.action = action;
    }

    @Override
    public String toString() {
        return "OrderCustomization{" +
                "id=" + id +
                ", orderItemId=" + (orderItem != null ? orderItem.getId() : "null") +
                ", ingredient=" + (ingredient != null ? ingredient.getName() : "null") +
                ", action='" + action + '\'' +
                '}';
    }
}
