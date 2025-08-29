package com.example.dao;

import com.example.model.PizzaVariant;

import java.util.List;

public interface PizzaVariantDao {
    PizzaVariant findById(int id);
    List<PizzaVariant> findByPizzaId(int pizzaId);

    PizzaVariant save(PizzaVariant v);
    PizzaVariant update(PizzaVariant v);
    boolean delete(int id);

    int deleteAllByPizzaId(int pizzaId);
    int bulkInsert(List<PizzaVariant> variants);
}
