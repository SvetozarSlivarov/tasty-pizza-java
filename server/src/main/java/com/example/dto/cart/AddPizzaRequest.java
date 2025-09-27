package com.example.dto.cart;

import java.util.List;

public record AddPizzaRequest(int productId, Integer variantId, int qty, String note,
                              List<Integer> removeIds, List<Integer> addIds) {}