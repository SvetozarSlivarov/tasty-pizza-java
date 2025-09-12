package com.example.dto;

import java.math.BigDecimal;

public record DrinkCreateRequest(
        String name,
        String description,
        BigDecimal price,
        Boolean isAvailable,
        String imageUrl

) {}