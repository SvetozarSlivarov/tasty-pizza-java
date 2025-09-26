// server/src/main/java/com/example/service/IngredientService.java
package com.example.service;

import com.example.dao.IngredientDao;
import com.example.dao.IngredientTypeDao;
import com.example.dao.impl.IngredientDaoImpl;
import com.example.dao.impl.IngredientTypeDaoImpl;
import com.example.exception.BadRequestException;
import com.example.exception.NotFoundException;
import com.example.model.Ingredient;
import com.example.model.IngredientType;

import java.util.List;

public class IngredientService {
    private final IngredientDao ingredientDao = new IngredientDaoImpl();
    private final IngredientTypeDao ingredientTypeDao = new IngredientTypeDaoImpl();

    public List<Ingredient> findAll() { return ingredientDao.findAll(); }
    public List<Ingredient> findByType(int typeId) { return ingredientDao.findByTypeId(typeId); }

    private IngredientType getTypeOrThrow(Integer typeId) {
        if (typeId == null) throw new BadRequestException("ingredient_type_required");
        IngredientType t = ingredientTypeDao.findById(typeId);
        if (t == null) throw new NotFoundException("ingredient_type_not_found");
        return t;
    }
    public Ingredient create(String name, Integer typeId) {
        if (name == null || name.trim().isEmpty())
            throw new BadRequestException("ingredient_name_required");

        IngredientType type = getTypeOrThrow(typeId);
        Ingredient toSave = new Ingredient(name.trim(), type);

        boolean ok = ingredientDao.save(toSave);
        if (!ok || toSave.getId() <= 0) {
            throw new RuntimeException("ingredient_create_failed");
        }

        return ingredientDao.findById(toSave.getId());
    }

    public Ingredient update(int id, String name, Integer typeId) {
        Ingredient current = ingredientDao.findById(id);
        if (current == null) throw new NotFoundException("ingredient_not_found");

        String newName = (name != null && !name.trim().isEmpty())
                ? name.trim()
                : current.getName();

        IngredientType type = (typeId != null)
                ? getTypeOrThrow(typeId)
                : current.getType();

        Ingredient toUpdate = new Ingredient(id, newName, type);
        boolean ok = ingredientDao.update(toUpdate);
        if (!ok) throw new RuntimeException("ingredient_update_failed");

        return ingredientDao.findById(id);
    }

    public boolean delete(int id) { return ingredientDao.delete(id); }

    public List<IngredientType> types() { return ingredientTypeDao.findAll(); }

    public int countIngredientsForType(int typeId) {
        return ingredientDao.countByTypeId(typeId);
    }
    public void deleteType(int id) {
        boolean ok = ingredientTypeDao.delete(id);
        if (!ok) throw new NotFoundException("ingredient_type_not_found");
    }

    public IngredientType createType(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("type_name_required");
        }
        IngredientType type = new IngredientType();
        type.setName(name.trim());

        boolean ok = ingredientTypeDao.save(type);
        if (!ok) throw new BadRequestException("type_create_failed");

        return type;
    }
}
