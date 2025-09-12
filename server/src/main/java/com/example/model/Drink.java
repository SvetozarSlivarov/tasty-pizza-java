package com.example.model;

import java.math.BigDecimal;

public class Drink {
    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean isAvailable;
    private String imageUrl;


    public Drink() {};
    public Drink(String name, String description, BigDecimal price, boolean isAvailable){
        this.name = name;
        this.description = description;
        this.price = price;
        this.isAvailable = isAvailable;
    };
    public Drink(int id, String name, String description, BigDecimal price, boolean isAvailable){
        this.id =id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isAvailable = isAvailable;
    };

    public int getId() {
        return id;
    }
    public  void setId(int id){
        this.id = id;
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public BigDecimal getPrice() {
        return this.price;
    }
    public void setPrice(BigDecimal price){
        this.price = price;
    }
    public boolean isAvailable(){
        return this.isAvailable;
    }
    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    @Override
    public String toString(){
        return "Drink{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", isAvailable=" + isAvailable +
                ", imageUrl = " + imageUrl +
                '}';
    }
}
