package com.example.dto;

import java.math.BigDecimal;

public record DrinkResponse(
        int id,
        String name,
        BigDecimal price,
        boolean available
) {}