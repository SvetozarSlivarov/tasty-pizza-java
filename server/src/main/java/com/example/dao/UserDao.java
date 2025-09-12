package com.example.dao;

import com.example.model.User;
import com.mysql.cj.util.DnsSrv;

import java.util.List;

public interface UserDao {

    User findById(int id);

    User findByUsername(String username);

    User findByUsernameAndPassword(String username, String password);

    List<User> findAll();
    boolean deleteById(int id);
    boolean save(User user);

    boolean update(User user);

    boolean delete(int id);
}
