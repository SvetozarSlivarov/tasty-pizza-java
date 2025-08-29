-- Ingredient types
INSERT INTO ingredient_types (id, name) VALUES
                                            (1, 'Cheese'),
                                            (2, 'Vegetable'),
                                            (3, 'Meat'),
                                            (4, 'Sauce');

-- Ingredients
INSERT INTO ingredients (id, name, type_id) VALUES
                                                (1, 'Mozzarella', 1),
                                                (2, 'Cheddar', 1),
                                                (3, 'Tomato Sauce', 4),
                                                (4, 'Mushrooms', 2),
                                                (5, 'Pepperoni', 3),
                                                (6, 'Olives', 2);

-- Products (pizzas + drinks)
INSERT INTO products (id, name, description, base_price, is_available, type) VALUES
                                                                                 (1, 'Margherita', 'Classic with mozzarella and tomato sauce', 8.50, TRUE, 'pizza'),
                                                                                 (2, 'Pepperoni',  'Cheese and spicy pepperoni',              9.50, TRUE, 'pizza'),
                                                                                 (3, 'Cola',       'Cola 0.5L',                               2.50, TRUE, 'drink'),
                                                                                 (4, 'Water',      'Mineral water 0.5L',                      1.50, TRUE, 'drink'),
                                                                                 (5, 'Beer',       'Lager 0.5L',                              3.50, TRUE, 'drink');

-- Pizzas (link to products)
INSERT INTO pizzas (product_id, spicy_level) VALUES
                                                 (1, 'mild'),
                                                 (2, 'medium');

-- Drinks (link to products)
INSERT INTO drinks (product_id) VALUES
                                    (3),
                                    (4),
                                    (5);

-- Pizza variants
INSERT INTO pizza_variants (id, pizza_id, size, dough, extra_price) VALUES
                                                                        (1, 1, 'small',  'classic',    0.00),
                                                                        (2, 1, 'medium', 'classic',    1.50),
                                                                        (3, 1, 'large',  'thin',       3.00),
                                                                        (4, 2, 'small',  'classic',    0.00),
                                                                        (5, 2, 'medium', 'classic',    2.00),
                                                                        (6, 2, 'large',  'wholegrain', 4.00);

-- Pizza base ingredients
INSERT INTO pizza_ingredients (pizza_id, ingredient_id, is_removable) VALUES
                                                                          (1, 1, TRUE),  -- Mozzarella
                                                                          (1, 3, FALSE), -- Tomato Sauce
                                                                          (2, 1, TRUE),  -- Mozzarella
                                                                          (2, 3, FALSE), -- Tomato Sauce
                                                                          (2, 5, FALSE); -- Pepperoni

-- Pizza allowed ingredients
INSERT INTO pizza_allowed_ingredients (pizza_id, ingredient_id) VALUES
                                                                    (1, 4), -- Mushrooms
                                                                    (1, 6), -- Olives
                                                                    (2, 4), -- Mushrooms
                                                                    (2, 6); -- Olives
