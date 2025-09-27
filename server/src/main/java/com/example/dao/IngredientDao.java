package com.example.dao;

import com.example.model.Ingredient;
import java.util.List;

public interface IngredientDao {
    Ingredient findById(int id);
    List<Ingredient> findAll();
    List<Ingredient> findByTypeId(int typeId);
    List<Ingredient> findByIds(List<Integer> ids);
    int countByTypeId(int typeId);
    boolean restore(int id);
    List<Ingredient> findActiveByIds(List<Integer> ids);
    List<Ingredient> findActiveByTypeId(int typeId);
    List<Ingredient> findActive();
    boolean save(Ingredient ingredient);
    boolean update(Ingredient ingredient);
    boolean delete(int id);
}
