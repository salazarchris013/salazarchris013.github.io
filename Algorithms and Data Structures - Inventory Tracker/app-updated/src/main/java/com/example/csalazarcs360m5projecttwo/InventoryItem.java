package com.example.csalazarcs360m5projecttwo;

// Simple model representing one inventory row, used for in-memory sorting/searching.

public class InventoryItem {

    private final int id;
    private final String name;
    private int quantity;
    private String description;

    public InventoryItem(int id, String name, int quantity, String description) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}