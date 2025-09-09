
-- Products
INSERT INTO products (id, type, name, description, base_price, is_available, created_at) VALUES
                                                                                             (1, 'pizza', 'Margherita', 'Classic tomato, mozzarella, basil', 10.90, TRUE, NOW()),
                                                                                             (2, 'pizza', 'Pepperoni',  'Mozzarella and pepperoni',          12.50, TRUE, NOW()),
                                                                                             (3, 'drink', 'Coca-Cola 0.33L', 'Can',                            2.90, TRUE, NOW()),
                                                                                             (4, 'drink', 'Water 0.5L',      'Still water',                    1.50, TRUE, NOW());

INSERT INTO pizzas (product_id, spicy_level) VALUES
                                                 (1, 'mild'),
                                                 (2, 'mild');

INSERT INTO drinks (product_id) VALUES
                                    (3), (4);

INSERT INTO pizza_variants (pizza_id, size, dough, extra_price) VALUES
-- Margherita (product_id = 1)
(1, 'small',  'classic',    0.00),
(1, 'medium', 'classic',    1.50),
(1, 'large',  'classic',    3.00),
-- Pepperoni (product_id = 2)
(2, 'small',  'classic',    0.00),
(2, 'medium', 'classic',    2.00),
(2, 'large',  'classic',    3.50);

-- Ingredient types
INSERT INTO ingredient_types (id, name) VALUES
                                            (1, 'Cheese'),
                                            (2, 'Sauce'),
                                            (3, 'Veggies'),
                                            (4, 'Meat');

-- Ingredients
INSERT INTO ingredients (id, name, type_id) VALUES
                                                (1, 'Mozzarella',   1),
                                                (2, 'Tomato Sauce', 2),
                                                (3, 'Basil',        3),
                                                (4, 'Pepperoni',    4),
                                                (5, 'Mushrooms',    3),
                                                (6, 'Onion',        3),
                                                (7, 'Olives',       3),
                                                (8, 'Extra Cheese', 1);

-- Pizza base ingredients (pizza_ingredients)
INSERT INTO pizza_ingredients (pizza_id, ingredient_id, is_removable) VALUES
-- Margherita (pizza_id = product_id = 1)
(1, 1, TRUE),  -- Mozzarella
(1, 2, FALSE), -- Tomato Sauce
(1, 3, TRUE),  -- Basil
-- Pepperoni (pizza_id = 2)
(2, 1, TRUE),  -- Mozzarella
(2, 2, FALSE), -- Tomato Sauce
(2, 4, TRUE);  -- Pepperoni

INSERT INTO pizza_allowed_ingredients (pizza_id, ingredient_id) VALUES
-- Margherita
(1, 5),  -- Mushrooms
(1, 6),  -- Onion
(1, 7),  -- Olives
(1, 8),  -- Extra Cheese
-- Pepperoni
(2, 5),
(2, 6),
(2, 7),
(2, 8);
