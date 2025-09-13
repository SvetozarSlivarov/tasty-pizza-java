
INSERT INTO products (type, name, description, base_price, is_available, image_url)
VALUES
    ('pizza', 'Margherita', 'Classic pizza with tomato and mozzarella', 8.50, TRUE, 'https://res.cloudinary.com/dea47xrrc/image/upload/products/file_wwwuxv.jpg'),
    ('pizza', 'Pepperoni',  'Spicy pepperoni and cheese',               9.90, TRUE, 'https://res.cloudinary.com/dea47xrrc/image/upload/products/file_wwwuxv.jpg'),
    ('drink', 'Coca Cola',  'Refreshing soda 500ml',                    2.50, TRUE, 'https://res.cloudinary.com/dea47xrrc/image/upload/products/file_ewsblu.jpg'),
    ('drink', 'Water',      'Still water 500ml',                        1.50, TRUE, 'https://res.cloudinary.com/dea47xrrc/image/upload/products/Water_nd6u3f.jpg');

INSERT INTO pizzas (product_id, spicy_level)
VALUES
    (1, 'mild'),
    (2, 'medium');

INSERT INTO drinks (product_id) VALUES (3), (4);

INSERT INTO pizza_variants (pizza_id, size, dough, extra_price)
VALUES
    (1, 'small',  'classic', 0.00),
    (1, 'medium', 'classic', 2.00),
    (1, 'large',  'thin',    3.50),

    (2, 'medium', 'classic', 0.00),
    (2, 'large',  'wholegrain', 2.50);

INSERT INTO ingredient_types (name)
VALUES ('cheese'), ('meat'), ('vegetable');

INSERT INTO ingredients (name, type_id)
VALUES
    ('Mozzarella', 1),
    ('Pepperoni',  2),
    ('Mushrooms',  3),
    ('Olives',     3);

INSERT INTO pizza_ingredients (pizza_id, ingredient_id, is_removable)
VALUES
    (1, 1, TRUE), -- Margherita -> Mozzarella
    (2, 1, TRUE), -- Pepperoni -> Mozzarella
    (2, 2, TRUE); -- Pepperoni -> Pepperoni

INSERT INTO pizza_allowed_ingredients (pizza_id, ingredient_id)
VALUES
    (1, 3),
    (1, 4),
    (2, 3),
    (2, 4);
