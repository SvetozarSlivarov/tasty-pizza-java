package com.example.dto;

import java.math.BigDecimal;

public record DrinkResponse(
        int id,
        String name,
        String description,
        BigDecimal price,
        boolean isAvailable,
        String imageUrl
) {}