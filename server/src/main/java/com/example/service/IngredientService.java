package com.example.service;

import com.example.dao.*;
import com.example.dao.impl.*;
import com.example.exception.*;
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
    public IngredientType getTypeOrThrow(int id) {
        IngredientType t = ingredientTypeDao.findById(id);
        if (t == null) throw new NotFoundException("ingredient_type_not_found");
        return t;
    }

    public IngredientType createType(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("type_name_required");
        }
        var type = new IngredientType();
        type.setName(name.trim());
        boolean ok = ingredientTypeDao.save(type);
        if (!ok) throw new BadRequestException("type_create_failed");
        return type;
    }

    public Ingredient create(String name, int typeId) {
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("ingredient_name_required");
        }
        var type = getTypeOrThrow(typeId);
        var ing = new Ingredient(name.trim(), type);
        boolean ok = ingredientDao.save(ing);
        if (!ok) throw new BadRequestException("ingredient_create_failed");
        return ing;
    }

    public List<IngredientType> types() { return ingredientTypeDao.findAll(); }
}
