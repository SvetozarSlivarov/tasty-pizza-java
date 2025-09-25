package com.example.dto.cart;

public record AddDrinkToCartRequest(
        int productId,
        int quantity,
        String note
) {}
