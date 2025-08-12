package com.example.dao;

import com.example.model.IngredientType;
import java.util.List;

public interface IngredientTypeDao {
    IngredientType findById(int id);
    List<IngredientType> findAll();
    boolean save(IngredientType type);
    boolean update(IngredientType type);
    boolean delete(int id);
}
