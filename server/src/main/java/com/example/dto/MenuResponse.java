package com.example.dto;
import java.util.List;

public record MenuResponse(
        List<PizzaResponse> pizzas,
        List<DrinkResponse> drinks
) {}