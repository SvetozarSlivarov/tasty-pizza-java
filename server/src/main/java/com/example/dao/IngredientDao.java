package com.example.dao;

import com.example.model.Ingredient;
import java.util.List;

public interface IngredientDao {
    Ingredient findById(int id);
    List<Ingredient> findAll();
    List<Ingredient> findByTypeId(int typeId);
    List<Ingredient> findByIds(List<Integer> ids);
    boolean save(Ingredient ingredient);
    boolean update(Ingredient ingredient);
    boolean delete(int id);
}
