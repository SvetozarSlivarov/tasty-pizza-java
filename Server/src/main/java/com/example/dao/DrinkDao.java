package com.example.dao;

import com.example.model.Drink;
import java.util.List;

public interface DrinkDao {
    Drink findById(int id);
    List<Drink> findAll();

    List<Drink> findAvailable();
    boolean save(Drink drink);
    boolean update(Drink drink);
    boolean delete(int id);
}
