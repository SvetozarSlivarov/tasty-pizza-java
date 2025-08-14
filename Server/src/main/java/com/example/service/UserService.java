package com.example.service;

import com.example.dao.UserDao;
import com.example.dao.impl.UserDaoImpl;
import com.example.model.User;
import com.example.model.enums.UserRole;
import com.example.security.Passwords;

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

    public boolean updateUser(User user) {
        return userDao.update(user);
    }

    public boolean deleteUser(int id) {
        return userDao.delete(id);
    }

    public Optional<User> login(String username, String passwordPlain) {
        User user = userDao.findByUsername(username);
        if (user == null) return Optional.empty();
        boolean ok = Passwords.verify(passwordPlain, user.getPassword());
        return ok ? Optional.of(user) : Optional.empty();
    }

    public boolean existsByUsername(String username) {
        return userDao.findByUsername(username) != null;
    }

    public boolean register(String fullname, String username, String passwordPlain) {
        if (existsByUsername(username)) return false;
        User user = new User(fullname, username, null, UserRole.CUSTOMER);
        user.setPassword(Passwords.hash(passwordPlain));
        return userDao.save(user);
    }
}
