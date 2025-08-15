package com.example.dao;

import com.example.model.Drink;
import java.util.List;

public interface DrinkDao {
    Drink findById(int id);
    List<Drink> findAll();

    List<Drink> findAvailable();
    Drink save(Drink drink);
    Drink update(Drink drink);
    Drink delete(int id);
}
