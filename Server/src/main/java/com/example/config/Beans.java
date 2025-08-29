package com.example.config;

import com.example.controller.*;
import com.example.security.JwtService;
import com.example.service.*;

public class Beans {
    public final JwtService jwt;
    public final UserService users = new UserService();
    public final PizzaService pizzas = new PizzaService();
    public final IngredientService ingredients = new IngredientService();

    public final PizzaIngredientService pizzaIngredient = new PizzaIngredientService();

    public final DrinkService drinks = new DrinkService();

    public final AuthController authCtl;
    public final UserController userCtl;
    public final PizzaController pizzaCtl;
    public final IngredientController ingredientCtl;
    public final IngredientTypeController ingredientTypeCtl;
    public final DrinkController drinkCtl;

    public Beans(ServerConfig cfg) {
        this.jwt = new JwtService(cfg.jwtBase64Secret(), cfg.jwtTtlSeconds());
        this.authCtl = new AuthController(users, jwt);
        this.userCtl = new UserController(users);
        this.pizzaCtl = new PizzaController(pizzas, pizzaIngredient ,jwt);
        this.ingredientCtl = new IngredientController(ingredients, jwt);
        this.ingredientTypeCtl = new IngredientTypeController(ingredients, jwt);
        this.drinkCtl = new DrinkController(drinks, jwt);
    }
}