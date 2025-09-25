package com.example.utils.mapper;

import com.example.dto.pizza.PizzaDto;
import com.example.dto.pizza.PizzaVariantDto;
import com.example.model.Pizza;
import com.example.model.PizzaVariant;
import com.example.model.enums.*;

import java.util.List;
import java.util.Objects;

public final class PizzaMapper {
    private PizzaMapper() {}

    public static PizzaDto toDto(Pizza p) {
        if (p == null) return null;
        return new PizzaDto(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.isAvailable(),
                p.getSpicyLevel() != null ? p.getSpicyLevel().name() : null,
                p.getImageUrl(),
                variantsToDto(p.getVariants())
        );
    }

    private static List<PizzaVariantDto> variantsToDto(List<PizzaVariant> variants) {
        if (variants == null) return null;
        return variants.stream()
                .map(v -> new PizzaVariantDto(
                        v.getId(),
                        v.getSize() != null ? v.getSize().name() : null,
                        v.getDough() != null ? v.getDough().name() : null,
                        v.getExtraPrice()
                ))
                .toList();
    }

    public static Pizza fromDto(PizzaDto dto) {
        if (dto == null) return null;
        Pizza p = new Pizza();
        if (dto.id() != null) p.setId(dto.id());
        p.setName(dto.name());
        p.setDescription(dto.description());
        p.setPrice(dto.basePrice());
        p.setAvailable(Boolean.TRUE.equals(dto.isAvailable()));
        if (dto.spicyLevel() != null) {
            p.setSpicyLevel(SpicyLevel.valueOf(dto.spicyLevel()));
        }
        p.setImageUrl(dto.imageUrl());
        return p;
    }

    public static List<PizzaVariant> variantsFromDto(List<PizzaVariantDto> dtos, int pizzaId) {
        if (dtos == null) return List.of();
        return dtos.stream().filter(Objects::nonNull).map(d -> {
            PizzaVariant v = new PizzaVariant();
            if (d.id() != null) v.setId(d.id());
            v.setPizzaId(pizzaId);
            if (d.size() != null) v.setSize(PizzaSize.valueOf(d.size()));
            if (d.dough() != null) v.setDough(DoughType.valueOf(d.dough()));
            v.setExtraPrice(d.extraPrice());
            return v;
        }).toList();
    }
}
