package com.example.dto;

import java.math.BigDecimal;

public record PizzaVariantDto(
        Integer id,
        String size,      // "small" | "medium" | "large"
        String dough,     // "thin" | "classic" | "wholegrain"
        BigDecimal extraPrice
) {}
