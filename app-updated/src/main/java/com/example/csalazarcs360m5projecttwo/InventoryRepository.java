package com.example.csalazarcs360m5projecttwo;

import android.content.Context;
import android.database.Cursor;

// InventoryRepository
// Handles all database access, keeping Activities separate from DatabaseHelper.

public class InventoryRepository {

    private final DatabaseHelper databaseHelper;

    public InventoryRepository(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    // User account operations
    public boolean registerUser(String username, String password) {
        return databaseHelper.registerUser(username, password);
    }

    public boolean loginUser(String username, String password) {
        return databaseHelper.loginUser(username, password);
    }

    // Inventory operation; returns all inventory items.

    public Cursor getAllItems() {
        return databaseHelper.getItems();
    }

    public void addItem(String name, int quantity, String description) {
        databaseHelper.addItem(name, quantity, description);
    }

    public void updateQuantity(int itemId, int newQuantity) {
        databaseHelper.updateItemQuantity(itemId, newQuantity);
    }

    public void updateDescription(int itemId, String newDescription) {
        databaseHelper.updateItemDescription(itemId, newDescription);
    }

    public void deleteItem(int itemId) {
        databaseHelper.deleteItem(itemId);
    }

    // Checks if an item should trigger a low stock alert

    public boolean isBelowThreshold(int quantity, int threshold) {
        return quantity <= threshold;
    }
}