INSERT IGNORE INTO ingredient_types(name)
VALUES ('cheese'), ('meat'), ('veggie'), ('sauce');

INSERT IGNORE INTO ingredients(name, type_id)
VALUES
('mozzarella', (SELECT id FROM ingredient_types WHERE name='cheese')),
('parmesan',   (SELECT id FROM ingredient_types WHERE name='cheese')),
('pepperoni',  (SELECT id FROM ingredient_types WHERE name='meat')),
('ham',        (SELECT id FROM ingredient_types WHERE name='meat')),
('bacon',      (SELECT id FROM ingredient_types WHERE name='meat')),
('basil',      (SELECT id FROM ingredient_types WHERE name='veggie')),
('mushrooms',  (SELECT id FROM ingredient_types WHERE name='veggie')),
('onion',      (SELECT id FROM ingredient_types WHERE name='veggie')),
('pineapple',  (SELECT id FROM ingredient_types WHERE name='veggie')),
('tomato_sauce', (SELECT id FROM ingredient_types WHERE name='sauce')),
('bbq_sauce',    (SELECT id FROM ingredient_types WHERE name='sauce'));

INSERT IGNORE INTO pizzas(name, description, base_price, is_available) VALUES
('Margherita', 'Classic tomato, mozzarella, basil', 8.50, TRUE),
('Pepperoni',  'Pepperoni, mozzarella, tomato sauce', 9.90, TRUE),
('Hawaiian',   'Ham, pineapple, mozzarella', 10.50, TRUE),
('Veggie',     'Mushrooms, onion, basil, mozzarella', 9.20, TRUE);

INSERT IGNORE INTO drinks(name, description, price, is_available) VALUES
('Coca-Cola 0.5L', 'Chilled', 2.50, TRUE),
('Mineral Water 0.5L', 'Still', 1.50, TRUE),
('Orange Juice 0.33L', 'Fresh', 2.20, TRUE);

-- Връзки за пиците
INSERT IGNORE INTO pizza_ingredients(pizza_id, ingredient_id, is_removable)
SELECT p.id, i.id, FALSE
FROM pizzas p, ingredients i
WHERE p.name='Margherita' AND i.name='tomato_sauce';

INSERT IGNORE INTO pizza_ingredients(pizza_id, ingredient_id, is_removable)
SELECT p.id, i.id, FALSE
FROM pizzas p, ingredients i
WHERE p.name='Margherita' AND i.name='mozzarella';

INSERT IGNORE INTO pizza_ingredients(pizza_id, ingredient_id, is_removable)
SELECT p.id, i.id, TRUE
FROM pizzas p, ingredients i
WHERE p.name='Margherita' AND i.name='basil';

-- Pepperoni
INSERT IGNORE INTO pizza_ingredients(pizza_id, ingredient_id, is_removable)
SELECT p.id, i.id, FALSE
FROM pizzas p, ingredients i
WHERE p.name='Pepperoni' AND i.name='tomato_sauce';

INSERT IGNORE INTO pizza_ingredients(pizza_id, ingredient_id, is_removable)
SELECT p.id, i.id, FALSE
FROM pizzas p, ingredients i
WHERE p.name='Pepperoni' AND i.name='mozzarella';

INSERT IGNORE INTO pizza_ingredients(pizza_id, ingredient_id, is_removable)
SELECT p.id, i.id, TRUE
FROM pizzas p, ingredients i
WHERE p.name='Pepperoni' AND i.name='pepperoni';

-- Hawaiian
INSERT IGNORE INTO pizza_ingredients(pizza_id, ingredient_id, is_removable)
SELECT p.id, i.id, FALSE
FROM pizzas p, ingredients i
WHERE p.name='Hawaiian' AND i.name='mozzarella';

INSERT IGNORE INTO pizza_ingredients(pizza_id, ingredient_id, is_removable)
SELECT p.id, i.id, TRUE
FROM pizzas p, ingredients i
WHERE p.name='Hawaiian' AND i.name='ham';

INSERT IGNORE INTO pizza_ingredients(pizza_id, ingredient_id, is_removable)
SELECT p.id, i.id, TRUE
FROM pizzas p, ingredients i
WHERE p.name='Hawaiian' AND i.name='pineapple';

-- Veggie
INSERT IGNORE INTO pizza_ingredients(pizza_id, ingredient_id, is_removable)
SELECT p.id, i.id, FALSE
FROM pizzas p, ingredients i
WHERE p.name='Veggie' AND i.name='tomato_sauce';

INSERT IGNORE INTO pizza_ingredients(pizza_id, ingredient_id, is_removable)
SELECT p.id, i.id, FALSE
FROM pizzas p, ingredients i
WHERE p.name='Veggie' AND i.name='mozzarella';

INSERT IGNORE INTO pizza_ingredients(pizza_id, ingredient_id, is_removable)
SELECT p.id, i.id, TRUE
FROM pizzas p, ingredients i
WHERE p.name='Veggie' AND i.name='mushrooms';

INSERT IGNORE INTO pizza_ingredients(pizza_id, ingredient_id, is_removable)
SELECT p.id, i.id, TRUE
FROM pizzas p, ingredients i
WHERE p.name='Veggie' AND i.name='onion';

INSERT IGNORE INTO pizza_ingredients(pizza_id, ingredient_id, is_removable)
SELECT p.id, i.id, TRUE
FROM pizzas p, ingredients i
WHERE p.name='Veggie' AND i.name='basil';

INSERT IGNORE INTO pizza_allowed_ingredients(pizza_id, ingredient_id)
SELECT p.id, i.id FROM pizzas p, ingredients i;

