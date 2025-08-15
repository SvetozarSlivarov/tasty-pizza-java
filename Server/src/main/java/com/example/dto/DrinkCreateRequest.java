package com.example.dto;

import java.math.BigDecimal;

public record DrinkCreateRequest(
        String name,
        BigDecimal price,
        Boolean available
) {}