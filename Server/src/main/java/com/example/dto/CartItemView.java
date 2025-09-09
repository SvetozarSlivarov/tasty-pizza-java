package com.example.dto;
import java.math.BigDecimal;
import java.util.List;

public record CartItemView(
        int id,
        int productId,
        Integer variantId,
        int quantity,
        BigDecimal unitPrice,
        String note,
        List<CartCustomizationView> customizations
) {}