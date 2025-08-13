package com.example.service;

import com.example.dao.*;
import com.example.dao.impl.*;
import com.example.model.Drink;
import com.example.model.Ingredient;
import com.example.model.Pizza;
import com.example.model.dto.PizzaDetails;
import com.example.model.enums.UserRole;

import java.util.List;

public class MenuService {

    private final PizzaDao pizzaDao = new PizzaDaoImpl();
    private final DrinkDao drinkDao = new DrinkDaoImpl();
    private final IngredientDao ingredientDao = new IngredientDaoImpl();
    private final PizzaIngredientDao pizzaIngredientDao = new PizzaIngredientDaoImpl();
    private final PizzaAllowedIngredientDao allowedDao = new PizzaAllowedIngredientDaoImpl();

    public List<Pizza> listPizzasFor(UserRole role) {
        return (role == UserRole.ADMIN) ? pizzaDao.findAll() : pizzaDao.findAvailable();
    }

    public List<Drink> listDrinksFor(UserRole role) {
        return (role == UserRole.ADMIN) ? drinkDao.findAll() : drinkDao.findAvailable();
    }

    public PizzaDetails getPizzaDetails(int pizzaId, UserRole role) {
        Pizza pizza = pizzaDao.findById(pizzaId);
        if (pizza == null) return null;
        if (role != UserRole.ADMIN && !pizza.isAvailable()) return null;

        List<Integer> ingredientIds = pizzaIngredientDao.findIngredientIdsByPizzaId(pizzaId);
        List<Integer> allowedIds    = allowedDao.findIngredientIdsByPizzaId(pizzaId);

        List<Ingredient> ingredients = ingredientDao.findByIds(ingredientIds);
        List<Ingredient> allowed     = ingredientDao.findByIds(allowedIds);

        return new PizzaDetails(pizza, ingredients, allowed);
    }
}
