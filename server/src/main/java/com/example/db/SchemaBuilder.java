package com.example.db;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class SchemaBuilder {

    public static void createAllTables() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            createUsersTable(stmt);
            createProductsTable(stmt);

            // marker субтипове
            createPizzasTable(stmt);
            createDrinksTable(stmt);

            // вариации за пиците
            createPizzaVariantsTable(stmt);

            createIngredientTypesTable(stmt);
            createIngredientsTable(stmt);
            createPizzaIngredientsTable(stmt);          // default toppings
            createPizzaAllowedIngredientsTable(stmt);   // allowed extras

            createOrdersTable(stmt);
            createOrderItemsTable(stmt);                // с опционален pizza_variant_id
            createOrderItemCustomizationsTable(stmt);

            System.out.println("All tables created or already exist.");
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

    private static void createProductsTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS products (
                id INT PRIMARY KEY AUTO_INCREMENT,
                type ENUM('pizza','drink') NOT NULL,
                name VARCHAR(100) NOT NULL,
                description TEXT,
                base_price DECIMAL(8,2) NOT NULL,
                is_available BOOLEAN DEFAULT TRUE,
                image_url VARCHAR(1024) NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        stmt.executeUpdate(sql);
    }

    // Маркерни таблици (само PK/FK към products)
    private static void createPizzasTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS pizzas (
                product_id INT PRIMARY KEY,
                spicy_level  ENUM('mild','medium','hot') NOT NULL,
                FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
            )
        """;
        stmt.executeUpdate(sql);
    }

    private static void createDrinksTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS drinks (
                product_id INT PRIMARY KEY,
                FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
            )
        """;
        stmt.executeUpdate(sql);
    }

    private static void createPizzaVariantsTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS pizza_variants (
                id INT PRIMARY KEY AUTO_INCREMENT,
                pizza_id INT NOT NULL,                                -- FK към pizzas(product_id)
                size ENUM('small','medium','large') NOT NULL,
                dough ENUM('thin','classic','wholegrain') NOT NULL,
                extra_price DECIMAL(8,2) NOT NULL DEFAULT 0.00,       -- добавка към base_price
                UNIQUE (pizza_id, size, dough),
                FOREIGN KEY (pizza_id) REFERENCES pizzas(product_id) ON DELETE CASCADE
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

    private static void createPizzaIngredientsTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS pizza_ingredients (
                pizza_id INT,
                ingredient_id INT,
                is_removable BOOLEAN NOT NULL DEFAULT TRUE,
                PRIMARY KEY (pizza_id, ingredient_id),
                FOREIGN KEY (pizza_id) REFERENCES pizzas(product_id) ON DELETE CASCADE,
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
                FOREIGN KEY (pizza_id) REFERENCES pizzas(product_id) ON DELETE CASCADE,
                FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
            )
        """;
        stmt.executeUpdate(sql);
    }

    private static void createOrdersTable(Statement stmt) throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS orders (
                id INT PRIMARY KEY AUTO_INCREMENT,
                user_id INT NULL,
                status ENUM('pending','preparing','delivered','cancelled') NOT NULL,
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
                product_id INT NOT NULL,
                pizza_variant_id INT NULL,
                quantity INT NOT NULL CHECK (quantity > 0),
                unit_price DECIMAL(8,2) NOT NULL,
                note TEXT,
                FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
                FOREIGN KEY (product_id) REFERENCES products(id),
                FOREIGN KEY (pizza_variant_id) REFERENCES pizza_variants(id) ON DELETE SET NULL
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
                action ENUM('add','remove') NOT NULL,
                FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE CASCADE,
                FOREIGN KEY (ingredient_id) REFERENCES ingredients(id)
            )
        """;
        stmt.executeUpdate(sql);
    }
}
