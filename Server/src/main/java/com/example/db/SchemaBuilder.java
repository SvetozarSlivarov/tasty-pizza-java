package com.example.db;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class SchemaBuilder {

    public static void createAllTables() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            createUsersTable(stmt);
            createPizzasTable(stmt);
            createDrinksTable(stmt);
            createIngredientTypesTable(stmt);
            createIngredientsTable(stmt);
            createPizzaIngredientsTable(stmt);
            createPizzaAllowedIngredientsTable(stmt);
            createOrdersTable(stmt);
            createOrderItemsTable(stmt);
            createOrderItemCustomizationsTable(stmt);

            System.out.println("Аll tables created or already exist.");

        } catch (SQLException e) {
            System.err.println("Error while creating tables:");
            e.printStackTrace();
        }
    }

    private static void createUsersTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INT PRIMARY KEY AUTO_INCREMENT,
                fullname VARCHAR(100) NOT NULL,
                username VARCHAR(50) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                role VARCHAR(20) NOT NULL,
                createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        stmt.executeUpdate(sql);
    }

    private static void createPizzasTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS pizzas (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(100) NOT NULL,
                description TEXT,
                base_price DECIMAL(6,2) NOT NULL,
                is_available BOOLEAN DEFAULT TRUE,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        stmt.executeUpdate(sql);
    }

    private static void createDrinksTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS drinks (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(100) NOT NULL,
                description TEXT,
                price DECIMAL(5,2) NOT NULL,
                is_available BOOLEAN DEFAULT TRUE
            )
        """;
        stmt.executeUpdate(sql);
    }

    private static void createIngredientsTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS ingredients (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(100) NOT NULL,
                type_id INT,
                FOREIGN KEY (type_id) REFERENCES ingredient_types(id)
            )
        """;
        stmt.executeUpdate(sql);
    }

    private static void createIngredientTypesTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS ingredient_types (
                id INT PRIMARY KEY AUTO_INCREMENT,
                name VARCHAR(50) NOT NULL
            )
        """;
        stmt.executeUpdate(sql);
    }

    private static void createPizzaIngredientsTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS pizza_ingredients (
                pizza_id INT,
                ingredient_id INT,
                PRIMARY KEY (pizza_id, ingredient_id),
                FOREIGN KEY (pizza_id) REFERENCES pizzas(id),
                FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
            )
        """;
        stmt.executeUpdate(sql);
    }

    private static void createPizzaAllowedIngredientsTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS pizza_allowed_ingredients (
                pizza_id INT,
                ingredient_id INT,
                PRIMARY KEY (pizza_id, ingredient_id),
                FOREIGN KEY (pizza_id) REFERENCES pizzas(id),
                FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
            )
        """;
        stmt.executeUpdate(sql);
    }

    private static void createOrdersTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS orders (
                id INT PRIMARY KEY AUTO_INCREMENT,
                user_id INT,
                status ENUM('pending', 'preparing', 'delivered', 'cancelled') NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES users(id)
            )
        """;
        stmt.executeUpdate(sql);
    }

    private static void createOrderItemsTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS order_items (
                id INT PRIMARY KEY AUTO_INCREMENT,
                order_id INT NOT NULL,
                product_type ENUM('pizza', 'drink') NOT NULL,
                product_id INT NOT NULL,
                quantity INT NOT NULL,
                FOREIGN KEY (order_id) REFERENCES orders(id)
            )
        """;
        stmt.executeUpdate(sql);
    }

    private static void createOrderItemCustomizationsTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS order_item_customizations (
                id INT PRIMARY KEY AUTO_INCREMENT,
                order_item_id INT NOT NULL,
                ingredient_id INT NOT NULL,
                action ENUM('add', 'remove') NOT NULL,
                FOREIGN KEY (order_item_id) REFERENCES order_items(id),
                FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
            )
        """;
        stmt.executeUpdate(sql);
    }
}