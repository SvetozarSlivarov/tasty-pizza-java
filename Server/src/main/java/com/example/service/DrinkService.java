package com.example.service;

import com.example.dao.DrinkDao;
import com.example.dao.impl.DrinkDaoImpl;
import com.example.model.Drink;

import java.util.List;

public class DrinkService {
    private final DrinkDao drinkDao;

    public DrinkService() {
        this.drinkDao = new DrinkDaoImpl();
    }

    public Drink getDrinkById(int id) {
        return drinkDao.findById(id);
    }

    public List<Drink> getAllDrinks() {
        return drinkDao.findAll();
    }
    public List<Drink> getAvailableDrinks() {
        return drinkDao.findAvailable();
    }

    public boolean addDrink(Drink drink) {
        return drinkDao.save(drink);
    }

    public boolean updateDrink(Drink drink) {
        return drinkDao.update(drink);
    }

    public boolean deleteDrink(int id) {
        return drinkDao.delete(id);
    }
}
