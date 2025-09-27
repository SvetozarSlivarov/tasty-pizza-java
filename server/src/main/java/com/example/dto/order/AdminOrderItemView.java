package com.example.dto.order;

import java.math.BigDecimal;
import java.util.List;

public record AdminOrderItemView(
        int id,
        int productId,
        Integer variantId,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        String note,
        String name,
        String imageUrl,
        String type,
        String variantLabel,
        List<AdminCartCustomizationView> customizations
) {}