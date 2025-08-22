package com.example.dto;
import java.util.List;

public record PizzaCompositionRequest(List<Integer> ingredientIds) {}