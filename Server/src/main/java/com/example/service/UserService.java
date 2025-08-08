package com.example.service;

import com.example.dao.UserDao;
import com.example.dao.impl.UserDaoImpl;
import com.example.model.User;
import com.example.model.enums.UserRole;
import com.google.gson.Gson;

import java.util.List;
import java.util.Optional;

public class UserService {
    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDaoImpl();
    }

    public User getUserById(int id) {
        return userDao.findById(id);
    }

    public User getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public boolean registerUser(User user) {
        // TODO: Add validation (User exist? and more)
        return userDao.save(user);
    }

    public boolean updateUser(User user) {
        return userDao.update(user);
    }

    public boolean deleteUser(int id) {
        return userDao.delete(id);
    }

    public Optional<User> login(String username, String password) {
        User user = userDao.findByUsernameAndPassword(username, password);
        return Optional.ofNullable(user);
    }

    public boolean existsByUsername(String username) {
        return userDao.findByUsername(username) != null;
    }

    public boolean register(String fullname, String username, String password) {
        if (existsByUsername(username)) return false;

        User user = new User(fullname, username, password, UserRole.CUSTOMER);
        return userDao.save(user);
    }
}
