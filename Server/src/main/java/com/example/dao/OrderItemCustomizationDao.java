package com.example.dao;

import com.example.model.OrderCustomization;
import com.example.model.enums.CustomizationAction;
import java.util.List;

public interface OrderItemCustomizationDao {
    List<OrderCustomization> findByOrderItemId(int orderItemId);
    boolean add(int orderItemId, int ingredientId, CustomizationAction action);
    boolean remove(int id);
}
