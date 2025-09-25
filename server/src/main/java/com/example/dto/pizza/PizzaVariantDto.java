package com.example.dto.pizza;

import java.math.BigDecimal;

public record PizzaVariantDto(
        Integer id,
        String size,
        String dough,
        BigDecimal extraPrice
) {}
