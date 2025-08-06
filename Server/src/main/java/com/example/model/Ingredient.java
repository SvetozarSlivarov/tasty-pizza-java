package com.example.model;

public class Ingredient {
    private int id;
    private String name;
    private IngredientType type;

    public Ingredient() {}

    public Ingredient(String name, IngredientType type) {
        this.name = name;
        this.type = type;
    }

    public Ingredient(int id, String name, IngredientType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public IngredientType getType() {
        return type;
    }

    public void setType(IngredientType type){
        this.type = type;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + (type != null ? type.getName() : "null") +
                '}';
    }
}
