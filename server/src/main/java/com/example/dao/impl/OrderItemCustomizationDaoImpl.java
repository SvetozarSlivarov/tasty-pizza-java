package com.example.dao.impl;

import com.example.dao.OrderItemCustomizationDao;
import com.example.dao.base.AbstractDao;
import com.example.model.Ingredient;
import com.example.model.OrderCustomization;
import com.example.model.OrderItem;
import com.example.model.enums.CustomizationAction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderItemCustomizationDaoImpl extends AbstractDao implements OrderItemCustomizationDao {

    @Override
    public List<OrderCustomization> findByOrderItemId(int orderItemId) {
        String sql = "SELECT id, order_item_id, ingredient_id, action FROM order_item_customizations WHERE order_item_id=?";
        try {
            return queryList(sql, preparedStatement -> preparedStatement.setInt(1, orderItemId), this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public OrderCustomization findById(int id) {
        String sql = "SELECT id, order_item_id, ingredient_id, action FROM order_item_customizations WHERE id=?";
        try {
            return queryOne(sql, preparedStatement -> preparedStatement.setInt(1, id), this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean add(OrderCustomization customization) {
        String sql = "INSERT INTO order_item_customizations(order_item_id, ingredient_id, action) VALUES(?,?,?)";
        try {
            int newId = updateReturningId(sql, preparedStatement -> {
                preparedStatement.setInt(1, customization.getOrderItem().getId());
                preparedStatement.setInt(2, customization.getIngredient().getId());
                preparedStatement.setString(3, customization.getAction().name());
            });
            if (newId > 0) customization.setId(newId);
            return newId > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean remove(int id) {
        String sql = "DELETE FROM order_item_customizations WHERE id=?";
        try {
            return update(sql, preparedStatement -> preparedStatement.setInt(1, id)) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private OrderCustomization map(ResultSet resultSet) throws SQLException {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(resultSet.getInt("order_item_id"));

        Ingredient ing = new Ingredient();
        ing.setId(resultSet.getInt("ingredient_id"));

        OrderCustomization orderCustomization = new OrderCustomization();
        orderCustomization.setId(resultSet.getInt("id"));
        orderCustomization.setOrderItem(orderItem);
        orderCustomization.setIngredient(ing);
        orderCustomization.setAction(CustomizationAction.valueOf(resultSet.getString("action").toUpperCase()));
        return orderCustomization;
    }
}
