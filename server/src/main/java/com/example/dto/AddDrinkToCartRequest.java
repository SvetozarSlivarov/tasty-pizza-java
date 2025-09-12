package com.example.dto;

public record AddDrinkToCartRequest(
        int productId,
        int quantity,
        String note
) {}
