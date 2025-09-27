package com.example.service;

import com.example.dao.*;
import com.example.dao.impl.*;
import com.example.dto.ingredient.IngredientTypeView;
import com.example.dto.pizza.PizzaIngredientView;
import com.example.exception.BadRequestException;
import com.example.exception.NotFoundException;
import com.example.model.Ingredient;
import com.example.model.PizzaIngredient;

import java.util.*;
import java.util.stream.Collectors;

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
        return ingredientDao.findActiveByIds(ids);
    }

    public List<Ingredient> listAllowedIngredientsAdmin(int pizzaId) {
        ensurePizzaExists(pizzaId);
        var ids = allowedDao.findIngredientIdsByPizzaId(pizzaId);
        return ingredientDao.findByIds(ids);
    }
    public boolean addIngredientToPizza(int pizzaId, int ingredientId, boolean isRemovable) {
        ensurePizzaExists(pizzaId);
        var ing = ingredientDao.findById(ingredientId);
        if (ing == null) throw new NotFoundException("ingredient_not_found");
        if (ing.isDeleted()) throw new BadRequestException("ingredient_deleted");
        return pizzaIngredientDao.add(pizzaId, ingredientId, isRemovable);
    }

    public boolean updateIngredientRemovability(int pizzaId, int ingredientId, boolean isRemovable) {
        ensurePizzaExists(pizzaId);
        var ing = ingredientDao.findById(ingredientId);
        if (ing == null) throw new NotFoundException("ingredient_not_found");
        if (ing.isDeleted()) throw new BadRequestException("ingredient_deleted");
        return pizzaIngredientDao.updateIsRemovable(pizzaId, ingredientId, isRemovable);
    }

    public boolean removeIngredientFromPizza(int pizzaId, int ingredientId) {
        ensurePizzaExists(pizzaId);
        if (ingredientDao.findById(ingredientId) == null) throw new NotFoundException("ingredient_not_found");
        return pizzaIngredientDao.remove(pizzaId, ingredientId);
    }

    public boolean allowIngredientForPizza(int pizzaId, int ingredientId) {
        ensurePizzaExists(pizzaId);
        var ing = ingredientDao.findById(ingredientId);
        if (ing == null) throw new NotFoundException("ingredient_not_found");
        if (ing.isDeleted()) throw new BadRequestException("addon_unavailable");
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
        if (links.isEmpty()) return Collections.emptyList();

        var ids = links.stream().map(PizzaIngredient::getIngredientId).toList();
        var ingredients = ingredientDao.findByIds(ids);

        var byId = new HashMap<Integer, Ingredient>();
        for (var ing : ingredients) byId.put(ing.getId(), ing);

        var out = new ArrayList<PizzaIngredientView>();
        for (var link : links) {
            var ing = byId.get(link.getIngredientId());
            if (ing == null || ing.isDeleted()) {
                continue;
            }
            IngredientTypeView typeDto = null;
            if (ing.getType() != null) {
                typeDto = new IngredientTypeView(ing.getType().getId(), ing.getType().getName());
            }
            out.add(new PizzaIngredientView(ing.getId(), ing.getName(), typeDto, link.isRemovable()));
        }

        out.sort(Comparator.comparing(PizzaIngredientView::name, Comparator.nullsLast(String::compareToIgnoreCase)));
        return out;
    }
    public List<PizzaIngredientView> listPizzaIngredientsViewAdmin(int pizzaId) {
        ensurePizzaExists(pizzaId);

        var links = pizzaIngredientDao.findByPizzaId(pizzaId);
        if (links.isEmpty()) return Collections.emptyList();

        var ids = links.stream().map(PizzaIngredient::getIngredientId).toList();
        var ingredients = ingredientDao.findByIds(ids);

        var byId = ingredients.stream().collect(Collectors.toMap(Ingredient::getId, i -> i));
        var out = new ArrayList<PizzaIngredientView>();

        for (var link : links) {
            var ing = byId.get(link.getIngredientId());
            String name = (ing != null) ? ing.getName() : null;
            if (ing != null && ing.isDeleted() && name != null) {
                name = name + " (deleted)";
            }
            IngredientTypeView typeDto = null;
            if (ing != null && ing.getType() != null) {
                typeDto = new IngredientTypeView(ing.getType().getId(), ing.getType().getName());
            }
            out.add(new PizzaIngredientView(link.getIngredientId(), name, typeDto, link.isRemovable()));
        }
        return out;
    }
}
