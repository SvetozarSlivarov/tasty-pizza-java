package com.example.dto.order;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record AdminOrderListItemView(
        int orderId,
        String orderNumber,
        String status,
        BigDecimal total,
        int itemCount,
        Timestamp orderedAt,
        String customerUsername,
        String deliveryPhone,
        String deliveryAddress
) {}
