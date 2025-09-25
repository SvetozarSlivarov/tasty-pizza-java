package com.example.dto.cart;

import com.example.dto.cart.CartItemView;

import java.math.BigDecimal;
import java.util.List;

public record CartView(
        int orderId,
        String status,
        List<CartItemView> items,
        BigDecimal total
) {}

