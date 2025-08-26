package com.example.service;

import com.example.dao.*;
import com.example.dao.impl.*;
import com.example.dto.*;
import com.example.exception.NotFoundException;
import com.example.model.Ingredient;
import com.example.model.PizzaIngredient;
import com.example.model.Pizza;
import com.example.dao.impl.PizzaDetails;
import com.example.model.enums.UserRole;
import com.example.utils.mapper.MenuMappers;

import java.util.*;
import java.util.stream.Collectors;

public class PizzaService {
    private final PizzaDao pizzaDao = new PizzaDaoImpl();
    private final IngredientDao ingredientDao = new IngredientDaoImpl();
    private final PizzaIngredientDao pizzaIngredientDao = new PizzaIngredientDaoImpl();
    private final PizzaAllowedIngredientDao allowedDao = new PizzaAllowedIngredientDaoImpl();

    // ===== Pizzas =====
    public List<Pizza> findAll(boolean availableOnly, UserRole role) {
        if (availableOnly || role != UserRole.ADMIN) return pizzaDao.findAvailable();
        return pizzaDao.findAll();
    }

    public Pizza create(PizzaCreateRequest req) {
        Pizza p = new Pizza(req.name(), req.description(), req.price(), Boolean.TRUE.equals(req.available()));
        return pizzaDao.save(p);
    }

    public Pizza update(int id, PizzaUpdateRequest req) {
        Pizza p = pizzaDao.findById(id);
        if (p == null) throw new NotFoundException("pizza_not_found");
        MenuMappers.applyUpdate(req, p);
        return pizzaDao.update(p);
    }

    public void delete(int id) {
        var baseIds = pizzaIngredientDao.findIngredientIdsByPizzaId(id);
        for (Integer ingId : baseIds) pizzaIngredientDao.remove(id, ingId);
        var allowIds = allowedDao.findIngredientIdsByPizzaId(id);
        for (Integer ingId : allowIds) allowedDao.disallow(id, ingId);
        Pizza deleted = pizzaDao.delete(id);
        if (deleted == null) throw new NotFoundException("pizza_not_found");
    }

    // ===== Details (pizza + ingredients + allowed) =====
    public PizzaDetails getDetails(int pizzaId, UserRole role) {
        Pizza pizza = pizzaDao.findById(pizzaId);
        if (pizza == null) return null;
        if (role != UserRole.ADMIN && !pizza.isAvailable()) return null;

        var ingredientIds = pizzaIngredientDao.findIngredientIdsByPizzaId(pizzaId);
        var allowedIds    = allowedDao.findIngredientIdsByPizzaId(pizzaId);

        var ingredients = ingredientDao.findByIds(ingredientIds);
        var allowed     = ingredientDao.findByIds(allowedIds);
        return new PizzaDetails(pizza, ingredients, allowed);
    }
    public List<PizzaIngredientView> listPizzaIngredientsView(int pizzaId) {
        ensurePizzaExists(pizzaId);

        var links = pizzaIngredientDao.findByPizzaId(pizzaId);
        var ids = links.stream().map(PizzaIngredient::getIngredientId).toList();
        var ingredients = ingredientDao.findByIds(ids);

        Map<Integer, Ingredient> byId = new HashMap<>();
        for (var ing : ingredients) byId.put(ing.getId(), ing);

        List<PizzaIngredientView> out = new ArrayList<>();
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

    public List<PizzaIngredient> listPizzaIngredients(int pizzaId) {
        ensurePizzaExists(pizzaId);
        return pizzaIngredientDao.findByPizzaId(pizzaId);
    }

    public List<Ingredient> listAllowedIngredients(int pizzaId) {
        ensurePizzaExists(pizzaId);
        var allowedIds = allowedDao.findIngredientIdsByPizzaId(pizzaId);
        return ingredientDao.findByIds(allowedIds);
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
    private void ensurePizzaExists(int id) {
        if (pizzaDao.findById(id) == null) throw new NotFoundException("pizza_not_found");
    }
    private static <T> Set<T> diff(Set<T> a, Set<T> b) {
        var s = new HashSet<>(a); s.removeAll(b); return s;
    }
}
