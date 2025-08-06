package com.example.model;

public class IngredientType {
    private int id;
    private String name;
    private String displayName;

    public IngredientType() {}

    public IngredientType(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    public IngredientType(int id, String name, String displayName) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName){
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "IngredientType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
