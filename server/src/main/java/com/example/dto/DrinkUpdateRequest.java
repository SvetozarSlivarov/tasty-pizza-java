package com.example.dto;

import java.math.BigDecimal;

public record DrinkUpdateRequest(
        String name,
        BigDecimal price,
        String description,
        Boolean isAvailable,
        String imageUrl
) {}