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
            return queryList(sql, ps -> ps.setInt(1, orderItemId), this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @Override
    public OrderCustomization findById(int id) {
        String sql = "SELECT id, order_item_id, ingredient_id, action FROM order_item_customizations WHERE id=?";
        try {
            return queryOne(sql, ps -> ps.setInt(1, id), this::map);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean add(OrderCustomization customization) {
        String sql = "INSERT INTO order_item_customizations(order_item_id, ingredient_id, action) VALUES(?,?,?)";
        try {
            int newId = updateReturningId(sql, ps -> {
                ps.setInt(1, customization.getOrderItem().getId());
                ps.setInt(2, customization.getIngredient().getId());
                ps.setString(3, customization.getAction().name()); // ADD / REMOVE
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
            return update(sql, ps -> ps.setInt(1, id)) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean removeByItemAndIngredient(int orderItemId, int ingredientId) {
        String sql = "DELETE FROM order_item_customizations WHERE order_item_id=? AND ingredient_id=?";
        try {
            return update(sql, ps -> {
                ps.setInt(1, orderItemId);
                ps.setInt(2, ingredientId);
            }) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- mapper ---
    private OrderCustomization map(ResultSet rs) throws SQLException {
        OrderItem oi = new OrderItem();
        oi.setId(rs.getInt("order_item_id"));

        Ingredient ing = new Ingredient();
        ing.setId(rs.getInt("ingredient_id"));

        OrderCustomization oc = new OrderCustomization();
        oc.setId(rs.getInt("id"));
        oc.setOrderItem(oi);
        oc.setIngredient(ing);
        oc.setAction(CustomizationAction.valueOf(rs.getString("action").toUpperCase()));
        return oc;
    }
}
