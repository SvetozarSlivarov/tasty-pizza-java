package com.example.dao;

import com.example.model.User;

import java.util.List;

public interface UserDao {

    User findById(int id);

    User findByUsername(String username);

    List<User> findAll();

    void save(User user);

    void update(User user);

    void delete(int id);
}
