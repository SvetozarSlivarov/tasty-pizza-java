package com.example.dao;

import com.example.model.User;

import java.util.List;

public interface UserDao {

    User findById(int id);

    User findByUsername(String username);

    List<User> findAll();
    boolean save(User user);

    boolean update(User user);

    boolean delete(int id);
}
