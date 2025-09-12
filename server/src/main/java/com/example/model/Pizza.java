package com.example.model;

import com.example.model.enums.*;
import java.math.BigDecimal;
import java.util.List;

public class Pizza {
    private int id;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private boolean isAvailable;
    private String imageUrl;

    private SpicyLevel spicyLevel;

    private List<PizzaVariant> variants;

    public Pizza() {}

    public Pizza(String name, String description, BigDecimal basePrice, boolean isAvailable) {
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.isAvailable = isAvailable;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return basePrice; }
    public void setPrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public SpicyLevel getSpicyLevel() { return spicyLevel; }
    public void setSpicyLevel(SpicyLevel spicyLevel) { this.spicyLevel = spicyLevel; }

    public List<PizzaVariant> getVariants() { return variants; }
    public void setVariants(List<PizzaVariant> variants) { this.variants = variants; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    @Override
    public String toString() {
        return "Pizza{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", basePrice=" + basePrice +
                ", isAvailable=" + isAvailable +
                ", imageUrl = " + imageUrl +
                ", spicyLevel=" + spicyLevel +
                ", variants=" + (variants != null ? variants.size() : 0) +
                '}';
    }
}
