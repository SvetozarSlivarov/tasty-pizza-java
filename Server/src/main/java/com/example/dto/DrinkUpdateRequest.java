package com.example.dto;

import java.math.BigDecimal;

public record DrinkUpdateRequest(
        String name,
        BigDecimal price,
        Boolean available
) {}