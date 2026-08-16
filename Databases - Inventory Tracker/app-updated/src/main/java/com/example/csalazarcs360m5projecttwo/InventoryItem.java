package com.example.csalazarcs360m5projecttwo;

// Stores one inventory item, including its category and supplier names.

public class InventoryItem {

    private final int id;
    private final String name;
    private int quantity;
    private String description;
    private String categoryName;
    private String supplierName;

    public InventoryItem(int id, String name, int quantity, String description) {
        this(id, name, quantity, description, null, null);
    }

    public InventoryItem(int id, String name, int quantity, String description, String categoryName, String supplierName) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.description = description;
        this.categoryName = categoryName;
        this.supplierName = supplierName;
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

    public String getCategoryName() {
        return categoryName;
    }

    public String getSupplierName() {
        return supplierName;
    }
}