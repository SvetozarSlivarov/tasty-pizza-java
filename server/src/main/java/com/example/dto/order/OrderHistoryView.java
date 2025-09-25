package com.example.dto.order;

import com.example.dto.cart.CartItemView;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public record OrderHistoryView(
        int orderId,
        String status,
        List<CartItemView> items,
        BigDecimal total,
        Timestamp orderedAt,
        Timestamp preparingAt,
        Timestamp outForDeliveryAt,
        Timestamp deliveredAt,
        Timestamp cancelledAt,
        String deliveryPhone,
        String deliveryAddress
) {}
