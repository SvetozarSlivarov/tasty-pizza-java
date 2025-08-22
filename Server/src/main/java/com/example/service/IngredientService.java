package com.example.service;

import com.example.dao.*;
import com.example.dao.impl.*;
import com.example.exception.NotFoundException;
import com.example.model.Ingredient;
import com.example.model.IngredientType;

import java.util.List;

public class IngredientService {
    private final IngredientDao ingredientDao = new IngredientDaoImpl();
    private final IngredientTypeDao ingredientTypeDao = new IngredientTypeDaoImpl();

    public List<Ingredient> findAll() { return ingredientDao.findAll(); }
    public List<Ingredient> findByType(int typeId) { return ingredientDao.findByTypeId(typeId); }

    public Ingredient create(Ingredient i) {
        boolean ok = ingredientDao.save(i);
        if (!ok) throw new RuntimeException("ingredient_save_failed");
        return i;
    }
    public Ingredient update(int id, Ingredient i) {
        i.setId(id);
        boolean ok = ingredientDao.update(i);
        if (!ok) throw new NotFoundException("ingredient_not_found");
        return i;
    }
    public void delete(int id) {
        boolean ok = ingredientDao.delete(id);
        if (!ok) throw new NotFoundException("ingredient_not_found");
    }

    // типове (ако ползваш CRUD за тях)
    public List<IngredientType> types() { return ingredientTypeDao.findAll(); }
}
