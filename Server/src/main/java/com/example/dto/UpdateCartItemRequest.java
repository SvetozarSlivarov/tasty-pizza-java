package com.example.dto;

public record UpdateCartItemRequest(
        Integer quantity,
        Integer variantId,
        String note
) {}
