package com.example.dao;

import com.example.model.OrderCustomization;
import com.example.model.enums.CustomizationAction;
import java.util.List;

public interface OrderItemCustomizationDao {
    List<OrderCustomization> findByOrderItemId(int orderItemId);
    OrderCustomization findById(int id);
    boolean removeByItemAndIngredient(int orderItemId, int ingredientId);
    boolean add(OrderCustomization customization);
    boolean remove(int id);
}
