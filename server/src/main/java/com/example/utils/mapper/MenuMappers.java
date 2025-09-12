package com.example.utils.mapper;

import com.example.dto.*;
import com.example.model.Pizza;
import com.example.model.Drink;

public class MenuMappers {

    public static PizzaResponse toDto(Pizza p) {
        return new PizzaResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.isAvailable());
    }

    public static DrinkResponse toDto(Drink d) {
        return new DrinkResponse(d.getId(), d.getName(),d.getDescription(), d.getPrice(), d.isAvailable());
    }

    public static Pizza fromCreate(PizzaCreateRequest r) {
        var p = new Pizza();
        p.setName(r.name());
        p.setDescription(r.description());
        p.setPrice(r.price());
        p.setAvailable(r.available() == null ? true : r.available());
        return p;
    }

    public static Drink fromCreate(DrinkCreateRequest r) {
        var d = new Drink();
        d.setName(r.name());
        d.setPrice(r.price());
        d.setAvailable(r.isAvailable() == null ? true : r.isAvailable());
        return d;
    }

    public static void applyUpdate(PizzaUpdateRequest r, Pizza p) {
        if (r.name() != null) p.setName(r.name());
        if (r.description() != null) p.setDescription(r.description());
        if (r.price() != null) p.setPrice(r.price());
        if (r.available() != null) p.setAvailable(r.available());
    }

    public static void applyUpdate(DrinkUpdateRequest r, Drink d) {
        if (r.name() != null) d.setName(r.name());
        if (r.price() != null) d.setPrice(r.price());
        if (r.isAvailable() != null) d.setAvailable(r.isAvailable());
    }
}
