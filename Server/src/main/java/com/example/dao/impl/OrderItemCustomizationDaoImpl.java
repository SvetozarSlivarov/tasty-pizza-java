package com.example.dao.impl;

import com.example.dao.OrderItemCustomizationDao;
import com.example.db.DBConnection;
import com.example.model.Ingredient;
import com.example.model.OrderCustomization;
import com.example.model.OrderItem;
import com.example.model.enums.CustomizationAction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemCustomizationDaoImpl implements OrderItemCustomizationDao {

    @Override
    public List<OrderCustomization> findByOrderItemId(int orderItemId) {
        String sql = "SELECT id, order_item_id, ingredient_id, action FROM order_item_customizations WHERE order_item_id=?";
        List<OrderCustomization> list = new ArrayList<>();
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, orderItemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(extract(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    @Override
    public boolean add(int orderItemId, int ingredientId, CustomizationAction action) {
        String sql = "INSERT INTO order_item_customizations(order_item_id, ingredient_id, action) VALUES(?,?,?)";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, orderItemId);
            ps.setInt(2, ingredientId);
            ps.setString(3, action.name()); // ADD/REMOVE
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean remove(int id) {
        String sql = "DELETE FROM order_item_customizations WHERE id=?";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private OrderCustomization extract(ResultSet rs) throws SQLException {
        OrderCustomization oc = new OrderCustomization();
        oc.setId(rs.getInt("id"));

        OrderItem item = new OrderItem();
        item.setId(rs.getInt("order_item_id"));
        oc.setOrderItem(item);

        Ingredient ing = new Ingredient();
        ing.setId(rs.getInt("ingredient_id"));
        oc.setIngredient(ing);

        String act = rs.getString("action");
        oc.setAction(CustomizationAction.valueOf(act.toUpperCase()));
        return oc;
    }
}
