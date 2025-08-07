package com.example.service;

import com.example.dao.PizzaDao;
import com.example.dao.impl.PizzaDaoImpl;
import com.example.model.Pizza;

import java.util.List;

public class PizzaService {
    private final PizzaDao pizzaDao;

    public PizzaService() {
        this.pizzaDao = new PizzaDaoImpl();
    }

    public Pizza getPizzaById(int id) {
        return pizzaDao.findById(id);
    }

    public List<Pizza> getAllPizzas() {
        return pizzaDao.findAll();
    }
    public List<Pizza> getAvailablePizzas() {
        return pizzaDao.findAvailable();
    }

    public boolean addPizza(Pizza pizza) {
        // TODO: Add validation
        return pizzaDao.save(pizza);
    }

    public boolean updatePizza(Pizza pizza) {
        // TODO: Add validation
        return pizzaDao.update(pizza);
    }

    public boolean deletePizza(int id) {
        return pizzaDao.delete(id);
    }
}
