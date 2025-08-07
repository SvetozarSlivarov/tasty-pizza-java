package com.example.dao;

import com.example.model.Pizza;
import java.util.List;

public interface PizzaDao {
    Pizza findById(int id);
    List<Pizza> findAll();
    boolean save(Pizza pizza);
    boolean update(Pizza pizza);
    boolean delete(int id);
}
