package com.example.model;

public class IngredientType {
    private int id;
    private String name;

    public IngredientType() {}

    public IngredientType(String name, String displayName) {
        this.name = name;
    }

    public IngredientType(int id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public String toString() {
        return "IngredientType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
