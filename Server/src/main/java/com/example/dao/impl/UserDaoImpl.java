package com.example.dao.impl;

import com.example.dao.UserDao;
import com.example.dao.base.AbstractDao;
import com.example.model.User;
import com.example.model.enums.UserRole;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl extends AbstractDao implements UserDao {

    @Override
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            return queryOne(sql, preparedStatement -> preparedStatement.setInt(1, id), this::map);
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try {
            return queryOne(sql, preparedStatement -> preparedStatement.setString(1, username), this::map);
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try {
            return queryOne(sql, preparedStatement -> {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password); }, this::map);
        } catch (SQLException e) { e.printStackTrace(); return null; }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY id";
        try {
            return queryList(sql, null, this::map);
        } catch (SQLException e) { e.printStackTrace(); return new ArrayList<>(); }
    }

    @Override
    public boolean save(User user) {
        String sql = "INSERT INTO users(fullname, username, password, role) VALUES(?,?,?,?)";
        try {
            int id = updateReturningId(sql, preparedStatement -> {
                preparedStatement.setString(1, user.getFullname());
                preparedStatement.setString(2, user.getUsername());
                preparedStatement.setString(3, user.getPassword());
                preparedStatement.setString(4, user.getRole().name());
            });
            if (id > 0) user.setId(id);
            return id > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET fullname=?, username=?, password=?, role=? WHERE id=?";
        try {
            int rows = update(sql, preparedStatement -> {
                preparedStatement.setString(1, user.getFullname());
                preparedStatement.setString(2, user.getUsername());
                preparedStatement.setString(3, user.getPassword());
                preparedStatement.setString(4, user.getRole().name());
                preparedStatement.setInt(5, user.getId());
            });
            return rows > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try {
            return update(sql, preparedStatement -> preparedStatement.setInt(1, id)) > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    private User map(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setFullname(resultSet.getString("fullname"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setRole(UserRole.valueOf(resultSet.getString("role").toUpperCase()));
        user.setCreatedAt(resultSet.getTimestamp("createdAt"));
        return user;
    }
}
