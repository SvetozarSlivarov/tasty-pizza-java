package com.example.db;

import com.example.security.Passwords;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SeedData {
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_FULLNAME = "Administrator";
    private static final String ADMIN_ROLE = "ADMIN";
    private static final String ADMIN_PLAIN_PASSWORD = "admin123";

    public static void ensureAdminUser() {
        try (Connection conn = DBConnection.getConnection()) {
            if (!userExists(conn, ADMIN_USERNAME)) {
                String hashed = Passwords.hash(ADMIN_PLAIN_PASSWORD);
                insertAdmin(conn, ADMIN_FULLNAME, ADMIN_USERNAME, hashed, ADMIN_ROLE);
                System.out.println("Seed: admin user created (" + ADMIN_USERNAME + ")");
            } else {
                System.out.println("Seed: admin user already exists (" + ADMIN_USERNAME + ")");
            }
        } catch (Exception e) {
            System.err.println("Seed: failed to ensure admin user");
            e.printStackTrace();
        }
    }

    private static boolean userExists(Connection conn, String username) throws Exception {
        String sql = "SELECT 1 FROM users WHERE username = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private static void insertAdmin(Connection conn, String fullname, String username,
                                    String hashedPassword, String role) throws Exception {
        String sql = "INSERT INTO users(fullname, username, password, role) VALUES(?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullname);
            ps.setString(2, username);
            ps.setString(3, hashedPassword);
            ps.setString(4, role);
            ps.executeUpdate();
        }
    }
}
