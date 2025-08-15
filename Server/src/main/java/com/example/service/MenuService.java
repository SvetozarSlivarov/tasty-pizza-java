package com.example.service;

import com.example.dao.*;
import com.example.dao.impl.*;
import com.example.dto.*;
import com.example.exception.NotFoundException;
import com.example.model.Drink;
import com.example.model.Ingredient;
import com.example.model.Pizza;
import com.example.model.dto.PizzaDetails;
import com.example.model.enums.UserRole;
import com.example.utils.mapper.MenuMappers;
import com.example.exception.NotFoundException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MenuService {

    private final PizzaDao pizzaDao = new PizzaDaoImpl();
    private final DrinkDao drinkDao = new DrinkDaoImpl();
    private final IngredientDao ingredientDao = new IngredientDaoImpl();
    private final PizzaIngredientDao pizzaIngredientDao = new PizzaIngredientDaoImpl();
    private final PizzaAllowedIngredientDao allowedDao = new PizzaAllowedIngredientDaoImpl();

    public List<PizzaResponse> listPizzasFor(UserRole role) {
        var src = (role == UserRole.ADMIN) ? pizzaDao.findAll() : pizzaDao.findAvailable();
        return src.stream().map(MenuMappers::toDto).collect(Collectors.toList());
    }

    public List<DrinkResponse> listDrinksFor(UserRole role) {
        var src = (role == UserRole.ADMIN) ? drinkDao.findAll() : drinkDao.findAvailable();
        return src.stream().map(MenuMappers::toDto).collect(Collectors.toList());
    }
    public MenuResponse listMenuFor(UserRole role) {
        return new MenuResponse(
                listPizzasFor(role),
                listDrinksFor(role)
        );
    }
    public PizzaResponse createPizza(PizzaCreateRequest req) {
        var entity = MenuMappers.fromCreate(req);
        var saved = pizzaDao.save(entity);
        return MenuMappers.toDto(saved);
    }
    public PizzaResponse updatePizza(int id, PizzaUpdateRequest req) {
        var entity = java.util.Optional.ofNullable(pizzaDao.findById(id))
                .orElseThrow(() -> new NotFoundException("Pizza " + id + " not found"));

        MenuMappers.applyUpdate(req, entity);

        var saved = pizzaDao.update(entity);
        if (saved == null) throw new RuntimeException("Could not update pizza " + id);

        return MenuMappers.toDto(saved);
    }
    public DrinkResponse createDrink(DrinkCreateRequest req) {
        var entity = MenuMappers.fromCreate(req);
        var saved = drinkDao.save(entity);
        return MenuMappers.toDto(saved);
    }
    public DrinkResponse updateDrink(int id, DrinkUpdateRequest req) {
        var entity = java.util.Optional.ofNullable(drinkDao.findById(id))
                .orElseThrow(() -> new NotFoundException("Drink " + id + " not found"));

        MenuMappers.applyUpdate(req, entity);
        var saved = drinkDao.update(entity);
        if (saved == null) throw new RuntimeException("Could not update pizza " + id);

        return MenuMappers.toDto(saved);
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
