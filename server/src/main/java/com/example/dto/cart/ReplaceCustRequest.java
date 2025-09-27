package com.example.dto.cart;

import java.util.List;

public record ReplaceCustRequest(List<Integer> removeIds, List<Integer> addIds) {}
