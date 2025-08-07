package com.example.dao.impl;

import com.example.dao.DrinkDao;
import com.example.db.DBConnection;
import com.example.model.Drink;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DrinkDaoImpl implements DrinkDao {

    @Override
    public Drink findById(int id) {
        String sql = "SELECT * FROM drinks WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return extractDrink(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Drink> findAll() {
        List<Drink> drinks = new ArrayList<>();
        String sql = "SELECT * FROM drinks";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                drinks.add(extractDrink(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return drinks;
    }
    @Override
    public List<Drink> findAvailable() {
        List<Drink> drinks = new ArrayList<>();
        String sql = "SELECT * FROM drinks WHERE is_available = true";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()){

            while (rs.next()){
                drinks.add(extractDrink(rs));
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return drinks;
    }

    @Override
    public boolean save(Drink drink) {
        String sql = "INSERT INTO drinks (name, description, price, is_available) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, drink.getName());
            ps.setString(2, drink.getDescription());
            ps.setBigDecimal(3, drink.getPrice());
            ps.setBoolean(4, drink.isAvailable());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean update(Drink drink) {
        String sql = "UPDATE drinks SET name = ?, description = ?, price = ?, is_available = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, drink.getName());
            ps.setString(2, drink.getDescription());
            ps.setBigDecimal(3, drink.getPrice());
            ps.setBoolean(4, drink.isAvailable());
            ps.setInt(5, drink.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM drinks WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    private Drink extractDrink(ResultSet rs) throws SQLException {
        return new Drink(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getBigDecimal("price"),
                rs.getBoolean("is_available")
        );
    }
}
