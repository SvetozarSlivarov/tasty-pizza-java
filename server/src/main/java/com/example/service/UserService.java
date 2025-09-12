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

    public Optional<User> findById(int id) {
        return Optional.ofNullable(userDao.findById(id));
    }

    public Optional<User> findByUsername(String username) {

        return Optional.ofNullable(userDao.findByUsername(username));
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public Optional<User> updateProfile(String username,
                                        String newFullname,
                                        String newUsername,
                                        String newPlainPassword) {
        User user = userDao.findByUsername(username);
        if (user == null) return Optional.empty();

        if (newFullname != null && !newFullname.isBlank()) user.setFullname(newFullname);

        if (newUsername != null && !newUsername.isBlank()) {
            if (!username.equals(newUsername) && existsByUsername(newUsername)) {
                return Optional.empty(); // username вече съществува
            }
            user.setUsername(newUsername);
        }

        if (newPlainPassword != null && !newPlainPassword.isBlank()) {
            user.setPassword(Passwords.hash(newPlainPassword));
        }

        boolean ok = userDao.update(user);
        return ok ? Optional.of(user) : Optional.empty();
    }

    public boolean deleteById(int userId) {
        return userDao.deleteById(userId);
    }

    public boolean deleteByUsername(String uname) {
        User u = userDao.findByUsername(uname);
        return (u != null) && userDao.deleteById(u.getId());
    }
    public Optional<User> setRoleById(int userId, UserRole role) {
        User u = userDao.findById(userId);
        if (u == null) return Optional.empty();
        u.setRole(role);
        return userDao.update(u) ? Optional.of(u) : Optional.empty();
    }

    public Optional<User> setRoleByUsername(String uname, UserRole role) {
        User u = userDao.findByUsername(uname);
        if (u == null) return Optional.empty();
        u.setRole(role);
        return userDao.update(u) ? Optional.of(u) : Optional.empty();
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
