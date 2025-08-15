package com.example.dto;

import java.math.BigDecimal;

public record PizzaUpdateRequest(
        String name,
        String description,
        BigDecimal price,
        Boolean available
) {}