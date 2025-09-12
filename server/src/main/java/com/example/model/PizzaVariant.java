package com.example.model;

import com.example.model.enums.*;

import java.math.BigDecimal;

public class PizzaVariant {
    private int id;
    private int pizzaId;
    private PizzaSize size;
    private DoughType dough;
    private BigDecimal extraPrice;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPizzaId() { return pizzaId; }
    public void setPizzaId(int pizzaId) { this.pizzaId = pizzaId; }

    public PizzaSize getSize() { return size; }
    public void setSize(PizzaSize size) { this.size = size; }

    public DoughType getDough() { return dough; }
    public void setDough(DoughType dough) { this.dough = dough; }

    public BigDecimal getExtraPrice() { return extraPrice; }
    public void setExtraPrice(BigDecimal extraPrice) { this.extraPrice = extraPrice; }
}
