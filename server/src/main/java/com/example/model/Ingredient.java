package com.example.model;

import java.sql.Timestamp;

public class Ingredient {
    private int id;
    private String name;
    private IngredientType type;
    private boolean deleted;
    private Timestamp deletedAt;

    public Ingredient() {}

    public Ingredient(String name, IngredientType type){
        this.name = name;
        this.type =type;
    }

    public Ingredient(int id, String name, IngredientType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IngredientType getType() {
        return type;
    }

    public void setType(IngredientType type) {
        this.type = type;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString() {
        return "Ingredient{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + (type != null ? type.getName() : null) +
                ", deleted=" + deleted +
                ", deletedAt=" + deletedAt +
                '}';
    }
}