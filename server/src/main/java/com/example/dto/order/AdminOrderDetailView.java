package com.example.dto.order;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public record AdminOrderDetailView(
        int orderId,
        String orderNumber,
        String status,
        List<AdminOrderItemView> items,
        BigDecimal total,
        Timestamp orderedAt,
        Timestamp preparingAt,
        Timestamp outForDeliveryAt,
        Timestamp deliveredAt,
        Timestamp cancelledAt,
        String customerUsername,
        String deliveryPhone,
        String deliveryAddress
) {}
