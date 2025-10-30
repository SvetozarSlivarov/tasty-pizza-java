package com.example.db;

import com.example.exception.DataInitializationException;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseInitializer {

    private static final String DB_NAME = "tasty_pizza";

    public static void initialize() {
        createDatabaseIfNotExists();

        try {
            SchemaBuilder.createAllTables();
            SeedData.ensureAdminUser();
        } catch (RuntimeException e){
            throw new DataInitializationException("Schema/seed step failed", e);
        }

    }

    private static void createDatabaseIfNotExists() {
        String rootUrl = getRootUrl();

        try (var conn = java.sql.DriverManager.getConnection(rootUrl);
             var stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("Database created or already exists: " + DB_NAME);

        } catch (SQLException e) {
            throw new DataInitializationException(
              "Failed to create database '" + DB_NAME + "' see cause for details.", e
            );
        }
    }

    private static String getRootUrl() {
        var resource = DBConnection.class.getClassLoader().getResourceAsStream("db.properties");
        if (resource == null) {
            throw new DataInitializationException("db.properties not found on classpath");
        }
        try (var input = resource) {
            var props = new java.util.Properties();
            props.load(input);

            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");

            if (user == null || user.isBlank() || pass == null || pass.isBlank()) {
                throw new DataInitializationException("db.user or db.password missing/empty in db.properties");
            }

            return "jdbc:mysql://localhost:3306/?user=" + user +
                    "&password=" + pass +
                    "&allowPublicKeyRetrieval=true&useSSL=false";
        } catch (IOException e) {
            throw new DataInitializationException("Failed to read db.properties", e);
        }
    }
}