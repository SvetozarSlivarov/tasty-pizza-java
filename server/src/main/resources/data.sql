INSERT INTO ingredient_types (id, name) VALUES
                                            (1,'Meat'),
                                            (2,'Vegetable'),
                                            (3,'Cheese'),
                                            (4,'Sauce'),
                                            (5,'Seafood'),
                                            (6,'Herbs & Spices'),
                                            (7,'Other');

INSERT INTO ingredients (id, name, type_id) VALUES
                                                (1,'Tomato sauce',4),
                                                (2,'BBQ Sauce',4),
                                                (3,'Pesto',4),

                                                (10,'Mozzarella',3),
                                                (11,'Cheddar',3),
                                                (12,'Parmesan',3),
                                                (13,'Blue cheese',3),
                                                (14,'Feta',3),
                                                (15,'Gorgonzola',3),

                                                (20,'Ham',1),
                                                (21,'Pepperoni',1),
                                                (22,'Chicken',1),
                                                (23,'Bacon',1),
                                                (24,'Prosciutto',1),
                                                (25,'Salami',1),

                                                (30,'Mushrooms',2),
                                                (31,'Onion',2),
                                                (32,'Olives',2),
                                                (33,'Bell peppers',2),
                                                (34,'Pineapple',2),
                                                (35,'Corn',2),
                                                (36,'Spinach',2),
                                                (37,'Arugula',2),
                                                (38,'Sun-dried tomatoes',2),
                                                (39,'Artichokes',2),
                                                (40,'Jalapeño',2),

                                                (50,'Tuna',5),

                                                (60,'Garlic',6),
                                                (61,'Oregano',6),
                                                (62,'Basil',6),
                                                (63,'Chili flakes',6);

INSERT INTO products (id, type, name, description, base_price, is_available, image_url) VALUES
                                                                                            (101,'pizza','Margherita','Classic tomato & mozzarella',6.50,TRUE,'https://res.cloudinary.com/dea47xrrc/image/upload/v1759067037/jonas-kakaroto-zlKdLdMREtE-unsplash_qpak4t.jpg'),
                                                                                            (102,'pizza','Pepperoni','Pepperoni & cheese',7.50,TRUE,'https://res.cloudinary.com/dea47xrrc/image/upload/v1759067038/david-foodphototasty-Xt84tIHbjRY-unsplash_spugs8.jpg'),
                                                                                            (103,'pizza','BBQ Chicken','Chicken, bacon & BBQ',8.50,TRUE,'https://res.cloudinary.com/dea47xrrc/image/upload/v1759067040/engin-akyurt-IfAb0bjhHlc-unsplash_wlezmr.jpg'),
                                                                                            (104,'pizza','Hawaiian','Ham & pineapple',7.80,TRUE,'https://res.cloudinary.com/dea47xrrc/image/upload/v1759067038/klara-kulikova-5eoiyhGLFb4-unsplash_ebgj4y.jpg'),
                                                                                            (105,'pizza','Veggie','Vegetable mix & olives',7.20,TRUE,'https://res.cloudinary.com/dea47xrrc/image/upload/v1759067039/fanny-gustafsson-odkIlTbKddI-unsplash_uqxejl.jpg'),
                                                                                            (106,'pizza','Four Cheese','Mozzarella, Cheddar, Parmesan, Blue',8.90,TRUE,'https://res.cloudinary.com/dea47xrrc/image/upload/v1759067042/saahil-khatkhate-kfDsMDyX1K0-unsplash_lqnfcf.jpg'),
                                                                                            (107,'pizza','Diavola','Spicy salami/pepperoni & chili',8.20,TRUE,'https://res.cloudinary.com/dea47xrrc/image/upload/v1759067041/lavi-perchik-LAJFSTJ5H-w-unsplash_arrj9h.jpg');

INSERT INTO pizzas (product_id, spicy_level) VALUES
                                                 (101,'mild'),
                                                 (102,'medium'),
                                                 (103,'hot'),
                                                 (104,'mild'),
                                                 (105,'mild'),
                                                 (106,'mild'),
                                                 (107,'hot');

INSERT INTO pizza_variants (pizza_id, size, dough, extra_price) VALUES
                                                                    (101,'small','classic',0.00),
                                                                    (101,'medium','classic',1.00),
                                                                    (101,'large','classic',2.00),
                                                                    (101,'medium','thin',1.20),

                                                                    (102,'small','classic',0.00),
                                                                    (102,'medium','classic',1.00),
                                                                    (102,'large','classic',2.00),
                                                                    (102,'medium','thin',1.20),

                                                                    (103,'small','classic',0.00),
                                                                    (103,'medium','classic',1.00),
                                                                    (103,'large','classic',2.00),
                                                                    (103,'medium','thin',1.20),

                                                                    (104,'small','classic',0.00),
                                                                    (104,'medium','classic',1.00),
                                                                    (104,'large','classic',2.00),
                                                                    (104,'medium','thin',1.20),

                                                                    (105,'small','classic',0.00),
                                                                    (105,'medium','classic',1.00),
                                                                    (105,'large','classic',2.00),
                                                                    (105,'medium','thin',1.20),

                                                                    (106,'small','classic',0.00),
                                                                    (106,'medium','classic',1.00),
                                                                    (106,'large','classic',2.00),
                                                                    (106,'medium','thin',1.20),

                                                                    (107,'small','classic',0.00),
                                                                    (107,'medium','classic',1.00),
                                                                    (107,'large','classic',2.00),
                                                                    (107,'medium','thin',1.20);

INSERT INTO pizza_ingredients (pizza_id, ingredient_id, is_removable) VALUES
                                                                          (101,1,FALSE),
                                                                          (101,10,TRUE),
                                                                          (101,62,TRUE),
                                                                          (101,61,TRUE);

INSERT INTO pizza_ingredients (pizza_id, ingredient_id, is_removable) VALUES
                                                                          (102,1,FALSE),
                                                                          (102,10,TRUE),
                                                                          (102,21,TRUE);

INSERT INTO pizza_ingredients (pizza_id, ingredient_id, is_removable) VALUES
                                                                          (103,2,FALSE),
                                                                          (103,10,TRUE),
                                                                          (103,22,TRUE),
                                                                          (103,23,TRUE),
                                                                          (103,31,TRUE);

INSERT INTO pizza_ingredients (pizza_id, ingredient_id, is_removable) VALUES
                                                                          (104,1,FALSE),
                                                                          (104,10,TRUE),
                                                                          (104,20,TRUE),
                                                                          (104,34,TRUE);

INSERT INTO pizza_ingredients (pizza_id, ingredient_id, is_removable) VALUES
                                                                          (105,1,FALSE),
                                                                          (105,10,TRUE),
                                                                          (105,30,TRUE),
                                                                          (105,31,TRUE),
                                                                          (105,33,TRUE),
                                                                          (105,32,TRUE),
                                                                          (105,35,TRUE);

INSERT INTO pizza_ingredients (pizza_id, ingredient_id, is_removable) VALUES
                                                                          (106,1,FALSE),
                                                                          (106,10,TRUE),
                                                                          (106,11,TRUE),
                                                                          (106,12,TRUE),
                                                                          (106,13,TRUE);

INSERT INTO pizza_ingredients (pizza_id, ingredient_id, is_removable) VALUES
                                                                          (107,1,FALSE),
                                                                          (107,10,TRUE),
                                                                          (107,25,TRUE),
                                                                          (107,21,TRUE),
                                                                          (107,40,TRUE),
                                                                          (107,63,TRUE);


INSERT INTO pizza_allowed_ingredients (pizza_id, ingredient_id) VALUES
                                                                    (101,12), (101,11), (101,38), (101,32), (101,30);

INSERT INTO pizza_allowed_ingredients (pizza_id, ingredient_id) VALUES
                                                                    (102,31), (102,40), (102,11), (102,12), (102,30);


INSERT INTO pizza_allowed_ingredients (pizza_id, ingredient_id) VALUES
                                                                    (103,35), (103,11), (103,23), (103,40);


INSERT INTO pizza_allowed_ingredients (pizza_id, ingredient_id) VALUES
                                                                    (104,11), (104,12), (104,23), (104,40);


INSERT INTO pizza_allowed_ingredients (pizza_id, ingredient_id) VALUES
                                                                    (105,36), (105,14), (105,39), (105,38), (105,60);


INSERT INTO pizza_allowed_ingredients (pizza_id, ingredient_id) VALUES
                                                                    (106,15), (106,24), (106,37);


INSERT INTO pizza_allowed_ingredients (pizza_id, ingredient_id) VALUES
                                                                    (107,63), (107,40), (107,31), (107,12);
INSERT INTO products (id, type, name, description, base_price, is_available, image_url) VALUES
                                                                                            (201,'drink','Coca-Cola 500ml','Soft drink',2.50,TRUE,'https://res.cloudinary.com/dea47xrrc/image/upload/v1759067036/mae-mu-z8PEoNIlGlg-unsplash_y3jr4b.jpg'),
                                                                                            (202,'drink','Sprite 500ml','Soft drink',2.50,TRUE,'https://res.cloudinary.com/dea47xrrc/image/upload/v1759067035/miheer-tewari-RH2ZA73kHiA-unsplash_ip5y60.jpg'),
                                                                                            (203,'drink','Fanta Orange 500ml','Soft drink',2.50,TRUE,'https://res.cloudinary.com/dea47xrrc/image/upload/v1759067035/khashayar-kouchpeydeh-nJguJaHo5dg-unsplash_rr96uj.jpg'),
                                                                                            (204,'drink','Mineral Water 500ml','Still water',1.50,TRUE,'https://res.cloudinary.com/dea47xrrc/image/upload/v1759067034/visual-karsa-pMds28MM7js-unsplash_plyjrs.jpg'),
                                                                                            (205,'drink','Beer Lager 500ml','Beer',3.50,TRUE,'https://res.cloudinary.com/dea47xrrc/image/upload/v1759067034/giovanna-gomes-Qy2KMPRV3X4-unsplash_rpdiqz.jpg');


INSERT INTO drinks (product_id) VALUES
                                    (201),(202),(203),(204),(205);