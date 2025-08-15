package com.example.dto;

import java.math.BigDecimal;

public record PizzaResponse(
        int id,
        String name,
        String description,
        BigDecimal price,
        boolean available
) {}