package com.example.dao;

import com.example.model.Pizza;
import com.example.model.PizzaVariant;
import java.util.List;

public interface PizzaDao {
    Pizza findById(int id);

    List<Pizza> findAll();
    List<Pizza> findAvailable();
    Pizza save(Pizza pizza);
    Pizza update(Pizza pizza);
    Pizza delete(int id);
}