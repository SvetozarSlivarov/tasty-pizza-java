package com.example.db;

public class DatabaseInitializer {

    private static final String DB_NAME = "tasty_pizza";

    public static void initialize() {
        createDatabaseIfNotExists();
        SchemaBuilder.createAllTables();
        SeedData.ensureAdminUser();
    }

    private static void createDatabaseIfNotExists() {
        String rootUrl = getRootUrl();

        try (var conn = java.sql.DriverManager.getConnection(rootUrl);
             var stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
            System.out.println("Database created or already exists: " + DB_NAME);

        } catch (Exception e) {
            System.err.println("Error while creating the database:");
            e.printStackTrace();
        }
    }

    private static String getRootUrl() {
        try (var input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            var props = new java.util.Properties();
            props.load(input);
            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");

            return "jdbc:mysql://localhost:3306/?user=" + user +
                    "&password=" + pass +
                    "&allowPublicKeyRetrieval=true&useSSL=false";
        } catch (Exception e) {
            throw new RuntimeException("Failed to build root DB URL from properties file", e);
        }
    }
}