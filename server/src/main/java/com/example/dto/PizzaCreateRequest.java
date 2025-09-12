package com.example.dto;

import java.math.BigDecimal;

public record PizzaCreateRequest(
        String name,
        String description,
        BigDecimal price,
        Boolean available
) {}