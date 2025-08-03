package com.example.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
public class DatabaseInitializer {

    private static final String DB_NAME = "tasty_pizza";

    public static void initialize() {
        createDatabaseIfNotExists();
        createTablesIfNotExist();
    }

    private static void createDatabaseIfNotExists() {
        String rootUrl = getRootUrl();

        try (Connection conn = DriverManager.getConnection(rootUrl);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("✅ Database created or already exists: " + DB_NAME);

        } catch (SQLException e) {
            System.err.println("❌ Error while creating the database:");
            e.printStackTrace();
        }
    }

    private static void createTablesIfNotExist() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT PRIMARY KEY AUTO_INCREMENT,
                    fullname VARCHAR(100) NOT NULL,
                    username VARCHAR(50) NOT NULL UNIQUE,
                    password VARCHAR(255) NOT NULL,
                    role VARCHAR(20) NOT NULL,
                    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;

            stmt.executeUpdate(createUsersTable);
            System.out.println("✅ Table 'users' created or already exists.");

        } catch (SQLException e) {
            System.err.println("❌ Error while creating the 'users' table:");
            e.printStackTrace();
        }
    }

    private static String getRootUrl() {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            Properties props = new Properties();
            props.load(input);

            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");

            return "jdbc:mysql://localhost:3306/?user=" + user +
                    "&password=" + pass +
                    "&allowPublicKeyRetrieval=true&useSSL=false";
        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to build root DB URL from properties file", e);
        }
    }
}
