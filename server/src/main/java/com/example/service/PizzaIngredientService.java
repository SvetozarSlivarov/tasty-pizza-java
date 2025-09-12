package com.example.service;

import com.example.dao.*;
import com.example.dao.impl.*;
import com.example.dto.IngredientTypeView;
import com.example.dto.PizzaIngredientView;
import com.example.exception.NotFoundException;
import com.example.model.Ingredient;
import com.example.model.PizzaIngredient;

import java.util.List;

public class PizzaIngredientService {

    private final PizzaDao pizzaDao = new PizzaDaoImpl();
    private final IngredientDao ingredientDao = new IngredientDaoImpl();
    private final PizzaIngredientDao pizzaIngredientDao = new PizzaIngredientDaoImpl();
    private final PizzaAllowedIngredientDao allowedDao = new PizzaAllowedIngredientDaoImpl();

    private void ensurePizzaExists(int id) {
        if (pizzaDao.findById(id) == null) throw new NotFoundException("pizza_not_found");
    }


    public List<PizzaIngredient> listPizzaIngredients(int pizzaId) {
        ensurePizzaExists(pizzaId);
        return pizzaIngredientDao.findByPizzaId(pizzaId);
    }


    public List<Ingredient> listAllowedIngredients(int pizzaId) {
        ensurePizzaExists(pizzaId);
        var ids = allowedDao.findIngredientIdsByPizzaId(pizzaId);
        return ingredientDao.findByIds(ids);
    }


    public boolean addIngredientToPizza(int pizzaId, int ingredientId, boolean isRemovable) {
        ensurePizzaExists(pizzaId);
        if (ingredientDao.findById(ingredientId) == null) throw new NotFoundException("ingredient_not_found");
        return pizzaIngredientDao.add(pizzaId, ingredientId, isRemovable);
    }


    public boolean updateIngredientRemovability(int pizzaId, int ingredientId, boolean isRemovable) {
        ensurePizzaExists(pizzaId);
        if (ingredientDao.findById(ingredientId) == null) throw new NotFoundException("ingredient_not_found");
        return pizzaIngredientDao.updateIsRemovable(pizzaId, ingredientId, isRemovable);
    }


    public boolean removeIngredientFromPizza(int pizzaId, int ingredientId) {
        ensurePizzaExists(pizzaId);
        if (ingredientDao.findById(ingredientId) == null) throw new NotFoundException("ingredient_not_found");
        return pizzaIngredientDao.remove(pizzaId, ingredientId);
    }


    public boolean allowIngredientForPizza(int pizzaId, int ingredientId) {
        ensurePizzaExists(pizzaId);
        if (ingredientDao.findById(ingredientId) == null) throw new NotFoundException("ingredient_not_found");
        return allowedDao.allow(pizzaId, ingredientId);
    }


    public boolean disallowIngredientForPizza(int pizzaId, int ingredientId) {
        ensurePizzaExists(pizzaId);
        if (ingredientDao.findById(ingredientId) == null) throw new NotFoundException("ingredient_not_found");
        return allowedDao.disallow(pizzaId, ingredientId);
    }


    public boolean isIngredientRemovableForPizza(int pizzaId, int ingredientId) {
        ensurePizzaExists(pizzaId);
        return pizzaIngredientDao.findByPizzaId(pizzaId).stream()
                .anyMatch(pi -> pi.getIngredientId() == ingredientId && pi.isRemovable());
    }
    public List<PizzaIngredientView> listPizzaIngredientsView(int pizzaId) {
        ensurePizzaExists(pizzaId);

        var links = pizzaIngredientDao.findByPizzaId(pizzaId);
        var ids = links.stream().map(pi -> pi.getIngredientId()).toList();
        var ingredients = ingredientDao.findByIds(ids);

        var byId = new java.util.HashMap<Integer, Ingredient>();
        for (var ing : ingredients) byId.put(ing.getId(), ing);

        var out = new java.util.ArrayList<PizzaIngredientView>();
        for (var link : links) {
            var ing = byId.get(link.getIngredientId());
            String name = ing != null ? ing.getName() : null;
            IngredientTypeView typeDto = null;
            if (ing != null && ing.getType() != null) {
                typeDto = new IngredientTypeView(ing.getType().getId(), ing.getType().getName());
            }
            out.add(new PizzaIngredientView(link.getIngredientId(), name, typeDto, link.isRemovable()));
        }
        return out;
    }
}
