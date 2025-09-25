package com.example.dto.pizza;

import java.math.BigDecimal;
import java.util.List;

public record PizzaDto(
        Integer id,
        String name,
        String description,
        BigDecimal basePrice,
        Boolean isAvailable,
        String spicyLevel,
        String imageUrl,
        List<PizzaVariantDto> variants
) {}
